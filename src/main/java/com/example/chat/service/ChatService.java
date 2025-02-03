package com.example.chat.service;

import com.example.chat.Exception.custom.InvalidChatException;
import com.example.chat.common.BaseResponseStatus;
import com.example.chat.config.MemberServiceClient;
import com.example.chat.dto.MemberResponse;

import com.example.chat.dto.request.GetMessageReq;
import com.example.chat.dto.request.StartChatReq;
import com.example.chat.dto.response.GetChatMessageRes;
import com.example.chat.dto.response.GetChatRoomRes;
import com.example.chat.dto.response.StartChatRes;
import com.example.chat.entity.Chat;
import com.example.chat.entity.ChatRoom;
import com.example.chat.file.service.CloudFileUploadService;
import com.example.chat.repository.ChatRepository;
import com.example.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.cglib.core.Local;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberServiceClient memberServiceClient;
    private final CloudFileUploadService cloudFileUploadService;

    // 회원서버에서 UUID로 사용자 정보 가져오기
    private MemberResponse getMemberById(String memberId) {
        UUID uuidMemberId;
        try {
            uuidMemberId = UUID.fromString(memberId);
        } catch (IllegalArgumentException e) {
            throw new InvalidChatException(BaseResponseStatus.CHAT_INVALID_USER_ID);
        }

        ResponseEntity<MemberResponse> response = memberServiceClient.getMemberById(uuidMemberId.toString());
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            return response.getBody();
        } else {
            throw new InvalidChatException(BaseResponseStatus.CHAT_INVALID_USER_ID);
        }
    }


    // 새로운 채팅방 생성(1:1)
    @Transactional
    public StartChatRes startChat(String userId, StartChatReq startChatReq){
        // 현재 사용자 정보 가져오기
        MemberResponse currentUser = getMemberById(userId);
        // 상대방 사용자 정보 가져오기
        List<String> recipientIds = startChatReq.getRecipientIds();
        if(recipientIds.contains(userId.toString())){ // 자기자신과의 채팅방 생성 금지
            throw new InvalidChatException(BaseResponseStatus.CHAT_SELF_CHAT);
        }

        // 기존 채팅방이 있는지 확인
        List<UUID> participants = new ArrayList<>();
        participants.add(currentUser.getId());
        for(String recipientId: recipientIds){
            MemberResponse recipient = getMemberById(recipientId);
            participants.add(recipient.getId());
        }
        // 같은 참여자가 있는지 확인
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findByParticipants(participants);

        ChatRoom chatRoom;
        if(chatRoomOptional.isEmpty()){
            String roomName = participants.size() > 2 ? "group chat": "1:1 chat";
            chatRoom = ChatRoom.builder()
                    .roomName(roomName)
                    .participants(participants)
                    .build();
            chatRoomRepository.save(chatRoom);
        } else {
            chatRoom = chatRoomOptional.get();
        }
        return StartChatRes.builder()
                .chatRoomId(chatRoom.getId())
                .recipientIds(recipientIds)
                .build();
    }


    // 채팅 메시지 저장
    @Transactional
    public void saveChat(GetMessageReq getMessageReq, Long chatRoomId, MultipartFile imgFile){
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()->new InvalidChatException(BaseResponseStatus.CHAT_INVALID_CHATROOM_ID));
        // 메시지 발신자 확인
        Long senderId = getMessageReq.getSenderId();
        if(!chatRoom.getParticipants().contains(senderId)){
            throw new InvalidChatException(BaseResponseStatus.CHAT_INVALID_USER_ID);
        }
        // 이미지 업로드 처리
        String imageUrl = null;
        if(imgFile != null && !imageUrl.isEmpty()) {
            imageUrl = cloudFileUploadService.uploadImg(imgFile); // 이미지 업로드 후 url 반환
        }

        // 메시지 저장
        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .message(getMessageReq.getMessage())
                .sendTime(LocalDateTime.parse(getMessageReq.getSendTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .senderId(senderId)
                .imageUrl(imageUrl)
                .build();
        chatRepository.save(chat);
    }
    // 채팅방 목록 조회
    public List<GetChatRoomRes> getMyChatRoomList(UUID userId){
        // 사용자가 포함된 모든 채팅방 조회
        List<ChatRoom> myChatRooms = chatRoomRepository.findAllByParticipantsContaining(userId);
        // 채팅방 응답 리스트 생성
        List<GetChatRoomRes> myChatRoomResList = new ArrayList<>();
        for(ChatRoom chatRoom: myChatRooms){
            UUID recipientId = chatRoom.getParticipants().stream()
                    .filter(participantId -> !participantId.equals(userId))
                    .findFirst() // 1:1 채팅에서는 하나의 상대방만 존재
                    .orElse(null); // 상대방이 없을 경우 null

            String participantNames = "";
            if(recipientId!=null){
                MemberResponse recipient = getMemberById(String.valueOf(recipientId));
                participantNames = recipient.getName();
            }

            String lastMessage = "";
            LocalDateTime lastSendTime = LocalDateTime.now();
            List<Chat> chatList = chatRoom.getChatList();
            if(!chatList.isEmpty()) {
                Chat lastChat = chatList.stream()
                        .max(Comparator.comparing(Chat::getSendTime))
                        .orElseThrow();
                lastMessage  = lastChat.getMessage();
                lastSendTime = lastChat.getSendTime();
            }
            GetChatRoomRes chatRoomRes = GetChatRoomRes.builder()
//                    .chatRoomId(chatRoom.getId())
                    .recipientNickname(participantNames)
                    .recipientId(recipientId)
                    .lastMessage(lastMessage)
                    .lastMessageDay(lastSendTime)
                    .build();

            myChatRoomResList.add(chatRoomRes);
        }
        return myChatRoomResList;
    }

    // 채팅 메시지 조회
    public List<GetChatMessageRes> getChatMessageList(UUID userId, Long chatRoomId, Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size);
        // 채팅방 조회 및 사용자 검증
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()-> new InvalidChatException(BaseResponseStatus.CHAT_INVALID_CHATROOM_ID));
        if(!chatRoom.getParticipants().contains(userId)){
            throw new InvalidChatException(BaseResponseStatus.CHAT_INVALID_CHATROOM_ID);
        }
        // 메시지 조회 및 변환
        List<Chat> chatList = chatRepository.findByChatRoomIdOrderBySendTimeDesc(pageable, chatRoomId).stream().toList();
        List<GetChatMessageRes> getChatMessageResList = new ArrayList<>();
        for(Chat chat: chatList){
            GetChatMessageRes getChatMessageRes = GetChatMessageRes.builder()
                    .message(chat.getMessage())
                    .sendTime(chat.getSendTime())
                    .senderId(chat.getSenderId())
                    .build();
            getChatMessageResList.add(getChatMessageRes);
        }
        return getChatMessageResList;
    }
}


