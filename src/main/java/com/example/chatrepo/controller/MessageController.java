package com.example.chatrepo.controller;

import com.example.chatrepo.dto.req.GetMessageReq;
import com.example.chatrepo.service.ChatService;
//import com.example.chatrepo.service.KafkaProducerService;
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
//    private final KafkaProducerService kafkaProducerService;

    @MessageMapping("/message/{chatRoomId}")
    public void sendMessage(GetMessageReq getMessageReq, @DestinationVariable("chatRoomId") Long chatRoomId) {
        chatService.saveChat(getMessageReq,chatRoomId); // 채팅 내역 저장
//        kafkaProducerService.send(getMessageReq);
        //메세지 서비스 로직
    }
}
