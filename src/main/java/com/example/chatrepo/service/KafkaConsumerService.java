package com.example.chatrepo.service;

import com.example.chatrepo.dto.Sender;
import com.example.chatrepo.dto.req.GetMessageReq;
import com.example.chatrepo.dto.res.GetMessageRes;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

//@Service
//@RequiredArgsConstructor
//public class KafkaConsumerService {
//
//    private static final String TOPIC_NAME = "chat";
//
//    private final SimpMessageSendingOperations template;
//
//    private final ObjectMapper objectMapper = new ObjectMapper(); // ObjectMapper를 final로 선언
//
//    @KafkaListener(topics = TOPIC_NAME)
//    public void listenMessage(String jsonMessage) {
//        try {
//            // JSON 메시지를 GetMessageReq 객체로 변환
//            GetMessageReq getMessageReq = objectMapper.readValue(jsonMessage, GetMessageReq.class);
//
//            // GetMessageRes 객체 생성
//            GetMessageRes getMessageRes = GetMessageRes.builder()
//                    .messageId(UUID.randomUUID().getMostSignificantBits() & Long.MAX_VALUE) // 임의의 messageId 생성
//                    .chatRoomId(getMessageReq.getChatRoomId())
//                    .sender(List.of(Sender.builder()
//                            .userId(getMessageReq.getSenderId()) // Sender의 userId 매핑
//                            .username(getMessageReq.getSenderName()) // Sender의 username 매핑
//                            .avatarUrl(null) // avatarUrl은 null로 초기화 (필요 시 수정)
//                            .build()))
//                    .message(getMessageReq.getMessage())
//                    .attachments(new ArrayList<>()) // 첨부 파일 리스트는 빈 리스트로 초기화
//                    .timestamp(LocalDateTime.now()) // 현재 시간으로 설정
//                    .mentionedUserIds(new ArrayList<>()) // 멘션된 사용자 ID는 빈 리스트로 초기화
//                    .build();
//
//            // 웹 소켓 연결하고 있는 클라이언트들에게 메시지 전송
//            template.convertAndSend("/sub/chatroom/" + getMessageReq.getChatRoomId(), getMessageRes);
//        } catch (Exception e) {
//            throw new RuntimeException("예외 발생: " + e.getMessage(), e);
//        }
//    }
//}



