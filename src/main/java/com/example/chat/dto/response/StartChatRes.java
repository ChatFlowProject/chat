package com.example.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class StartChatRes {
    private Long chatRoomId;
    private List<String> recipientIds;
}
