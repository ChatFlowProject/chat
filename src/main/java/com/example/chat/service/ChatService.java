package com.example.chat.service;

import com.example.chat.Exception.custom.InvalidChatException;
import com.example.chat.common.BaseResponse;
import com.example.chat.common.BaseResponseStatus;
import com.example.chat.config.MemberServiceClient;
import com.example.chat.dto.MemberResponse;


import com.example.chat.dto.request.CreateRoomReq;
import com.example.chat.dto.request.GetMessageReq;
import com.example.chat.dto.request.StartChatReq;
import com.example.chat.dto.response.GetChatMessageRes;
import com.example.chat.dto.response.GetChatRoomRes;
import com.example.chat.dto.response.StartChatRes;
import com.example.chat.entity.Chat;
import com.example.chat.entity.ChatRoom;
import com.example.chat.repository.ChatRepository;
import com.example.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.cglib.core.Local;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;


@Service
@RequiredArgsConstructor
public class ChatService {
    private final ChatRepository chatRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberServiceClient memberServiceClient;

    // 회원서버에서 ID로 사용자 정보 가져오기
    private MemberResponse getMemberById(Long memberId){
        ResponseEntity<MemberResponse> response = memberServiceClient.getMemberByMemberId(String.valueOf(memberId));
        if(response.getStatusCode().is2xxSuccessful() && response.getBody() != null) return response.getBody();
        else throw new InvalidChatException(BaseResponseStatus.CHAT_INVALID_USER_ID);
    }

    // 새로운 채팅방 생성(1:1)
    @Transactional
    public StartChatRes startChat(Long userId, StartChatReq startChatReq){
        // 현재 사용자 정보 가져오기
        MemberResponse currentUser = getMemberById(userId);
        // 상대방 사용자 정보 가져오기
        MemberResponse recipient = getMemberById(startChatReq.getRecipientId());
        // 자기자신과의 채팅방 생성 금지
        if(recipient.getId().equals(currentUser.getId())){
            throw new InvalidChatException(BaseResponseStatus.CHAT_SELF_CHAT);
        }

        // 기존 채팅방이 있는지 확인
        List<Long> participants = List.of(currentUser.getId(), recipient.getId());
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findByParticipants(participants);

        ChatRoom chatRoom;
        if(chatRoomOptional.isEmpty()){
            chatRoom = ChatRoom.builder()
                    .roomName("1:1 Chat")
                    .participants(participants)
                    .build();
            chatRoomRepository.save(chatRoom);
        } else {
            chatRoom = chatRoomOptional.get();
        }
        return StartChatRes.builder()
                .chatRoomId(chatRoom.getId())
                .recipientId(recipient.getId())
                .build();
    }
    // 채팅방 생성(1:다)
    @Transactional
    public Long createChatRoom(CreateRoomReq createRoomReq){
        // 참여자 리스트 검증(중복 제거 및 최소 2명 이상)
        List<Long> paritipants = createRoomReq.getParticipantIds().stream().distinct().toList();
        if(paritipants.size() < 2) {
            throw new InvalidChatException(BaseResponseStatus.CHAT_INVALID_PARTICIPANTS);
        }
        ChatRoom chatRoom = ChatRoom.builder()
                .roomName(createRoomReq.getRoomName())
                .participants(paritipants)
                .build();
        chatRoomRepository.save(chatRoom);
        return chatRoom.getId();
    }


    // 채팅 메시지 저장
    @Transactional
    public void saveChat(GetMessageReq getMessageReq, Long chatRoomId){
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()->new InvalidChatException(BaseResponseStatus.CHAT_INVALID_CHATROOM_ID));
        // 메시지 발신자 확인
        Long senderId = getMessageReq.getSenderId();
        if(!chatRoom.getParticipants().contains(senderId)){
            throw new InvalidChatException(BaseResponseStatus.CHAT_INVALID_USER_ID);
        }
        // 메시지 저장
        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .message(getMessageReq.getMessage())
                .sendTime(LocalDateTime.parse(getMessageReq.getSendTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .senderId(senderId)
                .build();
        chatRepository.save(chat);
    }
    // 채팅방 목록 조회
    public List<GetChatRoomRes> getMyChatRoomList(Long userId){
        // 사용자가 포함된 모든 채팅방 조회
        List<ChatRoom> myChatRooms = chatRoomRepository.findAllByParticipantsContaining(userId);
        // 채팅방 응답 리스트 생성
        List<GetChatRoomRes> myChatRoomResList = new ArrayList<>();
        for(ChatRoom chatRoom: myChatRooms){
            Long recipientId = chatRoom.getParticipants().stream()
                    .filter(participantId -> !participantId.equals(userId))
                    .findFirst() // 1:1 채팅에서는 하나의 상대방만 존재
                    .orElse(null); // 상대방이 없을 경우 null

            String participantNames = "";
            if(recipientId!=null){
                MemberResponse recipient = getMemberById(recipientId);
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
                    .chatRoomId(chatRoom.getId())
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
    public List<GetChatMessageRes> getChatMessageList(Long userId, Long chatRoomId, Integer page, Integer size){
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


