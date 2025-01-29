package com.example.chat.dto.response;

import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
public class GetMessageRes {
    private String message;
    private String sendTime;
    private Long senderId;
}
