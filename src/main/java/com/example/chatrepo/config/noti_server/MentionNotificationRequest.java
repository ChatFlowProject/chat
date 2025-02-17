package com.example.chatrepo.config.noti_server;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Builder
public class MentionNotificationRequest {
    private List<UUID> receiverIds; // 멘션된 사용자 ID 리스트
    private UUID senderId;          // 메시지를 보낸 사용자 ID
    private String message;         // 메시지 내용
    private Long chatRoomId;        // 채팅방 ID
}
