package com.example.chat.controller;

import com.example.chat.common.BaseResponse;
import com.example.chat.config.MemberServiceClient;
import com.example.chat.dto.MemberResponse;
import com.example.chat.dto.request.StartChatReq;
import com.example.chat.dto.response.GetChatMessageRes;
import com.example.chat.dto.response.GetChatRoomRes;
import com.example.chat.dto.response.StartChatRes;
import com.example.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final MemberServiceClient memberServiceClient;

    // 채팅방 생성
    @PostMapping("/create")
    public BaseResponse<StartChatRes>  createChatRoom(@Valid @RequestBody StartChatReq startChatReq){
        ResponseEntity<MemberResponse> response = memberServiceClient.getMemberByMemberId("current");
        Long userId = response.getBody().getId();
        return new BaseResponse<>(chatService.startChat(userId, startChatReq));
    }
    // 채팅방 목록 조회
    @GetMapping("/chatRoomList")
    public BaseResponse<List<GetChatRoomRes>> chatRoomList(){
        // 현재 사용자 정보 조회
        ResponseEntity<MemberResponse> response = memberServiceClient.getMemberByMemberId("current");
        Long userId = response.getBody().getId();
        return new BaseResponse<>(chatService.getMyChatRoomList(userId));
    }
    // 메시지 조회
    @GetMapping("/messageList")
    public BaseResponse<List<GetChatMessageRes>> getChatMessageList(Long chatRoomId, Integer page, Integer size){
        // 현재 사용자 정보 조회
        ResponseEntity<MemberResponse> response = memberServiceClient.getMemberByMemberId("current");
        Long userId = response.getBody().getId();
        return new BaseResponse<>(chatService.getChatMessageList(userId, chatRoomId, page,size));
    }
}

