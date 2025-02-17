package com.example.chatrepo.controller;

import com.example.chatrepo.common.ApiResponse;
import com.example.chatrepo.common.ApiStatus;
import com.example.chatrepo.common.BaseResponse;
import com.example.chatrepo.common.MemberResponse;
import com.example.chatrepo.config.member_server.MemberServiceClient;
import com.example.chatrepo.dto.req.CreateChatRoomReq;
import com.example.chatrepo.dto.res.CreateChatRoomRes;
import com.example.chatrepo.dto.res.GetChatRoomRes;
import com.example.chatrepo.dto.res.SearchChatRoomRes;
import com.example.chatrepo.exception.custom.InvalidChatException;
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
    private final MemberServiceClient memberServiceClient;

    // 모든 멤버 조회 메서드
    public List<MemberResponse> getAllMembers() {
        ApiResponse<List<MemberResponse>> response = memberServiceClient.getAllMembers();

        if (response.status() != ApiStatus.SUCCESS || response.data() == null) {
            throw new RuntimeException("Failed to fetch members: " + response.message());
        }

        return response.data();
    }

    // 특정 멤버 정보를 ID로 조회하는 메서드
    private MemberResponse findMemberById(UUID memberId) {
        List<MemberResponse> members = getAllMembers();

        return members.stream()
                .filter(member -> member.getId().equals(memberId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Member not found for ID: " + memberId));
    }

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
    public BaseResponse<String> leaveChatRoom(@PathVariable Long chatRoomId, @RequestParam UUID userId) {
        try {
            chatService.leaveChatRoom(chatRoomId, userId);
            messagingTemplate.convertAndSend("/sub/chat/room/" + chatRoomId, "A user has left the chat room.");
            return new BaseResponse<>("Successfully left the chat room.");
        } catch (InvalidChatException e) {
            // 사용자 정의 예외 처리
            return new BaseResponse<>(e.getMessage());
        } catch (Exception e) {
            // 기타 서버 에러 처리
            return new BaseResponse<>("An unexpected error occurred.");
        }
    }
}