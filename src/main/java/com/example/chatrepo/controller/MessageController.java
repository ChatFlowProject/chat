package com.example.chatrepo.controller;

import com.example.chatrepo.dto.req.ChatMessageReq;
import com.example.chatrepo.dto.req.GetMessageReq;
import com.example.chatrepo.dto.res.ChatMessageRes;
import com.example.chatrepo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;


@RequiredArgsConstructor
@RestController
@RequestMapping("/message")
public class MessageController {
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    // 1. 특정 채팅방에 메시지 보내기
    @MessageMapping("/chat/{chatRoomId}")
    public void sendMessage(@DestinationVariable Long chatRoomId, ChatMessageReq messageRequest) {
        // 1. 서비스에서 메시지 처리 및 저장
        ChatMessageRes response = chatService.processMessage(chatRoomId, messageRequest);

        // 2. 해당 채팅방 구독자들에게 메시지 전송
        messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoomId, response);
    }
}
