package com.example.chatrepo.repository;

import com.example.chatrepo.entity.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    // 특정 사용자가 포함된 모든 채팅방 조회
    List<ChatRoom> findAllByParticipantsContaining(UUID userId);

    // 정확히 일치하는 참여자를 가진 채팅방 조회 (JPQL)
    @Query("SELECT c FROM ChatRoom c JOIN c.participants p WHERE p IN :participants GROUP BY c.id HAVING COUNT(p) = :size")
    List<ChatRoom> findChatRoomsByExactParticipants(@Param("participants") List<UUID> participants, @Param("size") long size);

    // 정확히 일치하는 참여자를 가진 채팅방 조회 (네이티브 쿼리)
    @Query(value = "SELECT c.* FROM chat_room c " +
            "JOIN chat_room_participants p ON c.id = p.chat_room_id " +
            "WHERE p.participant_id IN (:participants) " +
            "GROUP BY c.id " +
            "HAVING COUNT(p.participant_id) = :size", nativeQuery = true)
    List<ChatRoom> findChatRoomsByExactParticipantsNative(@Param("participants") List<UUID> participants, @Param("size") long size);

    @Query("SELECT cr FROM ChatRoom cr " +
            "JOIN cr.participants p " +
            "WHERE p IN :participants " +
            "GROUP BY cr.id " +
            "HAVING COUNT(p) = :size")
    List<ChatRoom> findByParticipants(@Param("participants") List<UUID> participants, @Param("size") long size);


    // 채팅방 이름에 키워드가 포함된 채팅방 검색 (대소문자 구분 없음)
    List<ChatRoom> findByRoomNameContainingIgnoreCase(String roomName);
}

