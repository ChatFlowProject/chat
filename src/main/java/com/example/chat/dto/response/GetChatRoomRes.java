package com.example.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
public class GetChatRoomRes {
    private UUID chatRoomId;
    private String recipientNickname;
    private UUID recipientId;
    private String lastMessage;
    private LocalDateTime lastMessageDay;
}
