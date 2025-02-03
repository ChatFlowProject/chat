package com.example.chat.controller;

import com.example.chat.common.BaseResponse;
import com.example.chat.config.MemberServiceClient;
import com.example.chat.dto.MemberResponse;
import com.example.chat.dto.request.CreateRoomReq;
import com.example.chat.dto.request.StartChatReq;
import com.example.chat.dto.response.GetChatMessageRes;
import com.example.chat.dto.response.GetChatRoomRes;
import com.example.chat.dto.response.StartChatRes;
import com.example.chat.service.ChatService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;


@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {
    private final ChatService chatService;
    private final MemberServiceClient memberServiceClient;
    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    // 사용자 ID 조회 메서드
    private UUID getUserId(String memberId) {
        logger.info("Fetching user ID for memberId: {}", memberId);
        ResponseEntity<MemberResponse> response = memberServiceClient.getMemberById(memberId);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            logger.error("Failed to fetch user ID for memberId: {}. Status: {}", memberId, response.getStatusCode());
            throw new RuntimeException("Unable to fetch user information for memberId: " + memberId);
        }

        UUID userId = response.getBody().getId();
        logger.info("Successfully fetched user ID: {} for memberId: {}", userId, memberId);
        return userId;
    }

    // 채팅방 생성
    @PostMapping("/create")
    public BaseResponse<StartChatRes> createChatRoom(@Valid @RequestBody StartChatReq startChatReq) {
        try {
            // 현재 사용자 ID 가져오기 (예: "current"는 현재 사용자 식별자)
            UUID userId = getUserId("current");
            logger.info("Creating chat room for user ID: {}", userId);

            StartChatRes result = chatService.startChat(userId.toString(), startChatReq);
            logger.info("Chat room created successfully with ID: {}", result.getChatRoomId());

            return new BaseResponse<>(result);
        } catch (Exception e) {
            logger.error("Error creating chat room: {}", e.getMessage(), e);
            return new BaseResponse<>();
        }
    }

    // 사용자의 채팅방 목록 조회
    @GetMapping("/rooms")
    public BaseResponse<List<GetChatRoomRes>> getChatRoomList() {
        try {
            // 현재 사용자 ID 가져오기
            UUID userId = getUserId("current");
            logger.info("Fetching chat rooms for user ID: {}", userId);

            List<GetChatRoomRes> chatRooms = chatService.getMyChatRoomList(userId);
            logger.info("Successfully fetched {} chat rooms for user ID: {}", chatRooms.size(), userId);

            return new BaseResponse<>(chatRooms);
        } catch (Exception e) {
            logger.error("Error fetching chat room list: {}", e.getMessage(), e);
            return new BaseResponse<>();
        }
    }

    // 특정 채팅방의 메시지 목록 조회
    @GetMapping("/messages")
    public BaseResponse<List<GetChatMessageRes>> getChatMessageList(
            @RequestParam Long chatRoomId,
            @RequestParam(defaultValue = "0") Integer page,
            @RequestParam(defaultValue = "20") Integer size) {

        try {
            // 현재 사용자 ID 가져오기
            UUID userId = getUserId("current");
            logger.info("Fetching messages for chat room ID: {} and user ID: {}", chatRoomId, userId);

            List<GetChatMessageRes> messages = chatService.getChatMessageList(userId, chatRoomId, page, size);
            logger.info("Successfully fetched {} messages for chat room ID: {}", messages.size(), chatRoomId);

            return new BaseResponse<>(messages);
        } catch (Exception e) {
            logger.error("Error fetching messages for chat room ID {}: {}", chatRoomId, e.getMessage(), e);
            return new BaseResponse<>();
        }
    }
}
