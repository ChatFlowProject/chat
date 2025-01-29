package com.example.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class StartChatRes {
    private Long chatRoomId;
    private Long recipientId;
}
