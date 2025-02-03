package com.example.chat.controller;

import com.example.chat.dto.request.GetMessageReq;
import com.example.chat.service.ChatService;
import com.example.chat.service.KafkaProducerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@Slf4j
public class MessageController {
    private final ChatService chatService;
    private final KafkaProducerService kafkaProducerService;

    @MessageMapping("/message/{chatRoomId}")
    public void sendMessage(GetMessageReq getMessageReq, @DestinationVariable("chatRoomId") Long chatRoomId, @RequestPart(value="imgFile", required = false)MultipartFile imgFile){
        chatService.saveChat(getMessageReq, chatRoomId, imgFile);
    }
}
