package com.example.chatrepo.dto.req;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@NoArgsConstructor
@Setter
public class GetMessageReq {
    private Long chatRoomId;
    private String message;
    private String sendTime;
    private String senderName;
    private UUID senderId;
}

