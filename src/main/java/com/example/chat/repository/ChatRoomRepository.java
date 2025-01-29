package com.example.chat.repository;

import com.example.chat.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    List<ChatRoom> findAllByUser1Id(Long userId);
    List<ChatRoom> findAllByUser2Id(Long userId);
    Optional<ChatRoom> findByUser1IdAndUser2Id(Long userId, Long user2Id);
}
