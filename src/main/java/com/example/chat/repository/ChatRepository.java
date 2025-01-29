package com.example.chat.repository;


import com.example.chat.entity.Chat;
import com.example.chat.entity.ChatRoom;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    Slice<Chat> findByChatRoomIdOrderBySendTimeDesc(Pageable pageable, Long chatRoomId);

    Chat findTopByChatRoomOrderBySendTimeDesc(ChatRoom chatRoom);

}
