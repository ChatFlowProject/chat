package com.example.chatrepo.dto;

import com.example.chatrepo.entity.Chat;
import com.example.chatrepo.entity.ChatRoom;

import java.io.Serializable;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class Message implements Serializable {
    private Long id;
    private String content;
    private Long chatRoomId;
    private Long senderId;
    private String senderName;
    private long sendTime; // Epoch time in milliseconds
    private Integer readCount;

    // 기존 메서드
    public void sendTimeAndSender(LocalDateTime sendTime, Long senderId, String senderName, Integer readCount) {
        this.senderName = senderName;
        this.sendTime = sendTime.atZone(ZoneId.of("Asia/Seoul")).toInstant().toEpochMilli();
        this.senderId = senderId;
        this.readCount = readCount;
    }

    public void setId(Long id) {
        this.id = id;
    }

    // 엔티티 변환 메서드
    public Chat toEntity(ChatRoom chatRoom) {
        return Chat.builder()
                .id(this.id) // ID는 보통 자동 생성되므로 null로 설정 가능
                .message(this.content)
                .senderId(UUID.fromString(String.valueOf(this.senderId))) // UUID로 변환
                .sendTime(LocalDateTime.ofInstant(Instant.ofEpochMilli(this.sendTime), ZoneId.of("Asia/Seoul")))
                .timestamp(LocalDateTime.now()) // 현재 시간으로 설정 (필요 시 수정 가능)
                .chatRoom(chatRoom) // ChatRoom 객체를 받아 설정
                .build();
    }
}
