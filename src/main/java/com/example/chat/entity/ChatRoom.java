package com.example.chat.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name="chat_room")
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String roomName;

    @Builder.Default
    @ElementCollection(fetch=FetchType.EAGER) // 참여자 리스트를 별도의 테이블에 저장
    private final List<UUID> participants = new ArrayList<>();

    // Chat과의 관계 설정 (1:N)
    @Builder.Default
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Chat> chatList = new ArrayList<>();
}

