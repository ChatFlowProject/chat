package com.example.chat.service;

import com.example.chat.dto.request.GetMessageReq;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KafkaProducerService {

    private static final String TOPIC_NAME = "chat";

    private final KafkaTemplate<String, String> kafkaTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public void send(GetMessageReq getMessageReq){
        try {
            String toJson = objectMapper.writeValueAsString(getMessageReq);
            kafkaTemplate.send(TOPIC_NAME, toJson);
        } catch (Exception e) {
            throw new RuntimeException("예외 발생 : " + e.getMessage());
        }
    }
}