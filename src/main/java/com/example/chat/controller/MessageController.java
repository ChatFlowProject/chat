package com.example.chat.controller;

import com.example.chat.dto.request.GetMessageReq;
import com.example.chat.service.ChatService;
import com.example.chat.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@Slf4j
public class MessageController {
    private final ChatService chatService;
    private final KafkaProducerService kafkaProducerService;

    @MessageMapping("/message/{chatRoomId}")
    public void sendMessage(GetMessageReq getMessageReq, @DestinationVariable("chatRoomId") Long chatRoomId){
        chatService.saveChat(getMessageReq, chatRoomId);
    }

}
