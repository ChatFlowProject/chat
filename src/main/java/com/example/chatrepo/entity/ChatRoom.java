package com.example.chatrepo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "chat_room")
public class ChatRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Column(nullable = false)
    private String roomName;

    // 참여자 목록 (UUID를 저장)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "chat_room_participants", joinColumns = @JoinColumn(name = "chat_room_id"))
    @Column(name = "participant_id", nullable = false)
    private List<UUID> participants = new ArrayList<>();

    // Chat과의 관계 설정 (1:N)
    @OneToMany(mappedBy = "chatRoom", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Chat> chatList = new ArrayList<>();
}

