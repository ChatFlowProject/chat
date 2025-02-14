package com.example.chatrepo.dto.res;

import com.example.chatrepo.dto.ChatRoomDetail;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchChatRoomRes {
    private String query; // 검색어
    private List<ChatRoomDetail> chatRooms; // 검색된 채팅방 목록
}
