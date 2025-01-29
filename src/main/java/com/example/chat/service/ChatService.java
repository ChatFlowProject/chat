package com.example.chat.service;

import com.example.chat.Exception.custom.InvalidChatException;
import com.example.chat.common.BaseResponse;
import com.example.chat.common.BaseResponseStatus;
import com.example.chat.config.MemberServiceClient;
import com.example.chat.dto.MemberResponse;


import com.example.chat.dto.request.GetMessageReq;
import com.example.chat.dto.request.StartChatReq;
import com.example.chat.dto.response.StartChatRes;
import com.example.chat.entity.Chat;
import com.example.chat.entity.ChatRoom;
import com.example.chat.repository.ChatRepository;
import com.example.chat.repository.ChatRoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    // 새로운 채팅방 생성
    public ChatRoom createNewChatRoom(Long loggedInId, Long targetUserId){
        // 로그인한 사용자 정보 가져오기
        MemberResponse loggedInUser = getMemberById(loggedInId);
        // 대상 사용자 정보 가져오기
        MemberResponse targetUser = getMemberById(targetUserId);
        ChatRoom newChatRoom = ChatRoom.builder()
                .user1Id(loggedInUser.getId())
                .user2Id(targetUser.getId())
                .build();
        return chatRoomRepository.save(newChatRoom);
    }
}


