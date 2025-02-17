package com.example.chatrepo.controller;

import com.example.chatrepo.dto.req.ChatMessageReq;
import com.example.chatrepo.dto.res.ChatMessageRes;
import com.example.chatrepo.service.ChatService;
import com.example.chatrepo.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
public class MessageController {
    private final ChatService chatService;
    private final KafkaProducerService kafkaProducerService;

    // 1. 특정 채팅방에 메시지 보내기
    @MessageMapping("/chat/{chatRoomId}")
    public void sendMessage(@DestinationVariable Long chatRoomId, ChatMessageReq messageRequest) {
        System.out.println("sendMessage called with chatRoomId: " + chatRoomId);
        System.out.println("Payload: " + messageRequest);
        ChatMessageRes response = chatService.processMessage(chatRoomId, messageRequest);
        kafkaProducerService.send(chatRoomId,messageRequest);
    }

    @PostMapping("/send/{chatRoomId}")
    public void sendMessageViaRest(@PathVariable Long chatRoomId, @RequestBody ChatMessageReq messageRequest) {
        kafkaProducerService.send(chatRoomId, messageRequest);
    }

}
