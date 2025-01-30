package com.example.chat.entity;

import com.example.chat.dto.MemberResponse;
import com.example.chat.service.ChatServiceIn;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;


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

    // Chat과의 관계 설정 (1:N)
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chatList = new ArrayList<>();
}

