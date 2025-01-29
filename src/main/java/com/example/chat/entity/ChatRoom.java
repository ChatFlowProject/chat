package com.example.chat.entity;

import com.example.chat.dto.MemberResponse;
import com.example.chat.service.ChatServiceIn;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long user1Id; // 로그인한 사용자의 Primary Key

    @Column(nullable = false)
    private Long user2Id; // 대상 사용자의 Primary Key

    public String getUserName(String username, ChatServiceIn userClient) {
        MemberResponse memberResponse = userClient.getMemberByUsername(username);
        if (memberResponse == null) {
            throw new IllegalArgumentException("멤버 정보를 찾을 수 없습니다: " + username);
        }
        return memberResponse.getName();
    }
}

