package com.example.chatrepo.service;

import com.example.chatrepo.dto.Sender;
import com.example.chatrepo.dto.req.ChatMessageReq;
import com.example.chatrepo.dto.req.GetMessageReq;
import com.example.chatrepo.dto.res.GetMessageRes;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class KafkaConsumerService {

    private static final String TOPIC_NAME = "chat";

    private final SimpMessageSendingOperations template;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @KafkaListener(topics = TOPIC_NAME)
    public void listenMessage(String jsonMessage) {
        try {
            // JSON 메시지를 파싱하여 chatRoomId와 ChatMessageReq 추출
            JsonNode rootNode = objectMapper.readTree(jsonMessage);
            Long chatRoomId = rootNode.get("chatRoomId").asLong(); // chatRoomId 추출
            ChatMessageReq chatMessageReq = objectMapper.treeToValue(rootNode.get("message"), ChatMessageReq.class); // ChatMessageReq 추출

            // GetMessageRes 객체 생성
            GetMessageRes getMessageRes = GetMessageRes.builder()
                    .messageId(System.currentTimeMillis()) // 메시지 ID를 현재 시간으로 설정 (임시)
                    .chatRoomId(chatRoomId) // 추출한 chatRoomId 사용
                    .sender(List.of(Sender.builder()
                            .userId(chatMessageReq.getUserId()) // 요청에서 userId 가져오기
                            .username("SenderUsername") // 발신자 이름 (수정 필요: 실제 데이터에서 가져오기)
                            .avatarUrl("https://example.com/avatar.png") // 발신자 아바타 URL (수정 필요)
                            .build()))
                    .message(chatMessageReq.getMessage()) // 메시지 내용
                    .attachments(chatMessageReq.getAttachments()) // 첨부 파일 리스트
                    .timestamp(LocalDateTime.now()) // 현재 시간 설정
                    .mentionedUserIds(chatMessageReq.getMentionedUserIds()) // 멘션된 사용자 ID 리스트
                    .build();

            // WebSocket을 통해 클라이언트들에게 메시지 전송
            template.convertAndSend("/sub/chatroom/" + chatRoomId, getMessageRes);
        } catch (Exception e) {
            throw new RuntimeException("예외 발생 : " + e.getMessage());
        }
    }
}

