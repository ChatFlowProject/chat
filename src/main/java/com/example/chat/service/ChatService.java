package com.example.chat.service;

import com.example.chat.Exception.custom.InvalidChatException;
import com.example.chat.common.BaseResponse;
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
import com.example.chat.repository.ChatRepository;
import com.example.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;

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

    // 새로운 채팅방 생성
    @Transactional
    public StartChatRes startChat(Long userId, StartChatReq startChatReq){
        // 현재 사용자 정보 가져오기
        MemberResponse currentUser = getMemberById(Long.valueOf("current"));
        // 상대방 사용자 정보 가져오기
        MemberResponse recipient = getMemberById(startChatReq.getRecipientId());
        // 자기자신과의 채팅방 생성 금지
        if(recipient.getId().equals(currentUser.getId())) throw new InvalidChatException(BaseResponseStatus.CHAT_SELF_CHAT);
        Long user1Id = Math.min(currentUser.getId(), recipient.getId());
        Long user2Id = Math.max(currentUser.getId(), recipient.getId());

        // 기존 채팅방이 있는지 확인
        Optional<ChatRoom> chatRoomOptional = chatRoomRepository.findByUser1IdAndUser2Id(user1Id, user2Id);
        ChatRoom chatRoom;
        if(chatRoomOptional.isEmpty()) {
            chatRoom = ChatRoom.builder()
                    .user1Id(user1Id)
                    .user2Id(user2Id)
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

    // 채팅 메시지 저장
    @Transactional
    public void saveChat(GetMessageReq getMessageReq, Long chatRoomId){
        // 채팅방 조회
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(()->new InvalidChatException(BaseResponseStatus.CHAT_INVALID_CHATROOM_ID));
        // 메시지 발신자 확인
        MemberResponse sender = getMemberById(getMessageReq.getSenderId());
        if(!chatRoom.getUser1Id().equals(sender.getId()) && !chatRoom.getUser2Id().equals(sender.getId())) {
            throw new InvalidChatException(BaseResponseStatus.CHAT_INVALID_USER_ID);
        }
        Chat chat = Chat.builder()
                .chatRoom(chatRoom)
                .message(getMessageReq.getMessage())
                .sendTime(LocalDateTime.parse(getMessageReq.getSendTime(), DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                .build();
        chatRepository.save(chat);
    }
    // 채팅방 목록 조회

    public List<GetChatRoomRes> getMyChatRoomList(Long userId){
        // 두 가지 조건으로 채팅방 목록 조회
        List<ChatRoom> myChatRoomList1 = chatRoomRepository.findAllByUser1Id(userId);
        List<ChatRoom> myChatRoomList2 = chatRoomRepository.findAllByUser2Id(userId);
        // 채팅방 응답 리스트 생성
        List<GetChatRoomRes> myChatRoomResList = new ArrayList<>();
        makeChatRoomResList(userId, myChatRoomList1, myChatRoomResList, true);
        makeChatRoomResList(userId, myChatRoomList2, myChatRoomResList, false);
        // 최근 메시지 시간 기준으로 정렬
        myChatRoomResList.sort((chatRoomRes1, chatRoomRes2)->chatRoomRes2.getLastMessageDay().compareTo(chatRoomRes1.getLastMessageDay()));
        return myChatRoomResList;
    }

    private void makeChatRoomResList(Long userId, List<ChatRoom> chatRooms, List<GetChatRoomRes> myChatRoomResList, boolean isUser1){
        for(ChatRoom chatRoom: chatRooms){
            // 상대방 사용자 정보 가져오기
            Long recipientId = isUser1 ? chatRoom.getUser2Id() : chatRoom.getUser1Id();
            MemberResponse recipient = getMemberById(recipientId);
            // 채팅 목록에서 마지막 메시지와 전송 시간 가져오기
            String lastMessage = "";
            LocalDateTime lastSendTime = LocalDateTime.now();
            List<Chat> chatList = chatRoom.getChatList();
            if(!chatList.isEmpty()){
                Chat lastChat = chatList.stream()
                        .max(Comparator.comparing(Chat::getSendTime))
                        .orElseThrow(); // 마지막 메시지 가져오기
                lastMessage = lastChat.getMessage();
                lastSendTime = lastChat.getSendTime();
            }
            GetChatRoomRes chatRoomRes = GetChatRoomRes.builder()
                    .chatRoomId(chatRoom.getId())
                    .lastMessage(lastMessage)
                    .lastMessageDay(lastSendTime)
                    .build();
            myChatRoomResList.add(chatRoomRes);
        }
    }
    // 채팅 메시지 조회
    public List<GetChatMessageRes> getChatMessageList(Long userId, Long chatRoomId, Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size);
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId).orElseThrow(()-> new InvalidChatException(BaseResponseStatus.CHAT_INVALID_CHATROOM_ID));
        if(!chatRoom.getUser1Id().equals(userId) && !chatRoom.getUser2Id().equals(userId)) {
            throw new InvalidChatException(BaseResponseStatus.CHAT_INVALID_CHATROOM_ID);
        }
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


