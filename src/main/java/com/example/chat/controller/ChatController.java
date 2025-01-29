package com.example.chat.controller;

import com.example.chat.common.BaseResponse;
import com.example.chat.config.MemberServiceClient;
import com.example.chat.dto.MemberResponse;
import com.example.chat.dto.request.StartChatReq;
import com.example.chat.dto.response.StartChatRes;
import com.example.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final MemberServiceClient memberServiceClient;

    // 채팅방 생성
    @PostMapping("/start")
    public BaseResponse<StartChatRes>  createChatRoom(@Valid @RequestBody StartChatReq startChatReq){
        ResponseEntity<MemberResponse> response = memberServiceClient.getMemberByMemberId("current");
        Long userId = response.getBody().getId();
        return new BaseResponse<>(chatService.startChat(userId, startChatReq));
    }

}

