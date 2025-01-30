package com.example.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetChatRoomRes {
    private Long chatRoomId;
    private String recipientNickname;
    private Long recipientId;
    private String lastMessage;
    private LocalDateTime lastMessageDay;
}
