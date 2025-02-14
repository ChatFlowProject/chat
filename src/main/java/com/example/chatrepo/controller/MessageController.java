package com.example.chatrepo.controller;

import com.example.chatrepo.dto.req.GetMessageReq;
import com.example.chatrepo.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RequiredArgsConstructor
@RestController
@RequestMapping("/message")
@Slf4j
public class MessageController {
    private final ChatService chatService;

    // 1. 채팅 메시지 보내기
    @PostMapping("/{chatRoomId}")
    public void sendMessage(GetMessageReq getMessageReq, @RequestPart(value="imgFile", required = false) MultipartFile imgFile){
        chatService.saveChat(getMessageReq, imgFile);
    }


    // 2. 메시지 삭제


}
