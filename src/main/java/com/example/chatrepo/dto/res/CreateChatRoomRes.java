package com.example.chatrepo.dto.res;

import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class CreateChatRoomRes {
    private Long chatRoomId;
    private String chatRoomName;
    private List<UUID> participants;
    private List<String> recipientName;
}