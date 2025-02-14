package com.example.chatrepo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChatRoomDetail {
    private Long chatRoomId;
    private String name;
    private List<Participant> participants;
}
