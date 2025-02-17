package com.example.chatrepo.service;

import com.example.chatrepo.dto.req.ChatMessageReq;
import com.example.chatrepo.dto.req.GetMessageReq;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private static final String TOPIC_NAME = "chat";

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void send(Long chatRoomId, ChatMessageReq chatMessageReq) {
        try {
            // chatRoomId를 포함한 메시지 생성
            Map<String, Object> messageWithChatRoomId = new HashMap<>();
            messageWithChatRoomId.put("chatRoomId", chatRoomId);
            messageWithChatRoomId.put("message", chatMessageReq);

            // 메시지를 JSON 문자열로 변환 후 전송
            String toJson = objectMapper.writeValueAsString(messageWithChatRoomId);
            kafkaTemplate.send(TOPIC_NAME, toJson);
        } catch (Exception e) {
            throw new RuntimeException("예외 발생 : " + e.getMessage());
        }
    }
}
