package com.example.chat.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class CreateRoomReq {
    private String roomName;
    private List<Long> participantIds;
}
