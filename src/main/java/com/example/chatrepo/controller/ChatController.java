package com.example.chatrepo.controller;

import com.example.chatrepo.common.BaseResponse;
import com.example.chatrepo.dto.req.CreateChatRoomReq;
import com.example.chatrepo.dto.res.CreateChatRoomRes;
import com.example.chatrepo.dto.res.GetChatRoomRes;
import com.example.chatrepo.dto.res.SearchChatRoomRes;
import com.example.chatrepo.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final SimpMessageSendingOperations messagingTemplate;

    // 1. 채팅방 생성
    @PostMapping("/create")
    public BaseResponse<CreateChatRoomRes> createChatRoom(@RequestBody CreateChatRoomReq createChatRoomReq) {
        CreateChatRoomRes createChatRoomRes = chatService.createChatRoom(createChatRoomReq);
        return new BaseResponse<>(createChatRoomRes);
    }
    // 2. 채팅방 목록 조회
    @GetMapping("/rooms")
    public BaseResponse<List<GetChatRoomRes>> getChatRoomList(@RequestParam UUID userId) {
        List<GetChatRoomRes> chatRooms = chatService.getMyChatRoomList(userId);
        return new BaseResponse<>(chatRooms);
    }
    // 3. 채팅방 검색
    @GetMapping("/search")
    public BaseResponse<SearchChatRoomRes> searchChatRooms(@RequestParam String query){
        SearchChatRoomRes searchChatRoomRes = chatService.searchChatRooms(query);
        return new BaseResponse<>(searchChatRoomRes);
    }
    // 4. 채팅방 나가기
    @PostMapping("/leave/{chatRoomId}")
    public BaseResponse<String> leaveChatRoom(@PathVariable Long chatRoomId, @RequestParam UUID userId){
        chatService.leaveChatRoom(chatRoomId, userId);
        // 메시지 브로커를 통해 알림 전송
        messagingTemplate.convertAndSend("/sub/chat/room/"+chatRoomId, "사용자가 채팅방을 나갔습니다.");
        return new BaseResponse<>("채팅방에서 성공적으로 나갔습니다.");
    }

}