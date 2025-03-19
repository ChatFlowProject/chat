package com.example.chatrepo.dto.res;

import com.example.chatrepo.dto.Participant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GetChatRoomRes {
    private Long chatRoomId; // 채팅방 ID
    private String chatRoomName; // 채팅방 이름
    private List<Participant> participants; // 참여자 목록
    private int participantsLength; // 참여자 수
}
