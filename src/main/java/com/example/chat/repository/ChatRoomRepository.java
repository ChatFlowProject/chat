package com.example.chat.repository;

import com.example.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findAllByParticipantsContaining(UUID userId);
    Optional<ChatRoom> findByParticipants(List<UUID> participants);
}
