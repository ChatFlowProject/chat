package com.example.chat.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@NoArgsConstructor
@Setter
public class GetMessageReq {
    private Long chatRoomId;
    private String message;
    private String sendTime;
    private String senderName;
    private Long senderId;
}
