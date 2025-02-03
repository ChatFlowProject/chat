package com.example.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class GetChatMessageRes {
    private String message;
    private LocalDateTime sendTime;
    private Long senderId;
    private String imageUrl;
}
