package com.example.chatrepo.entity;

import com.example.chatrepo.dto.Attachment;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Table(name = "chat")
@EntityListeners(AuditingEntityListener.class)
public class Chat {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @CreatedDate
    private LocalDateTime sendTime;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String message;

    private String imageUrl;

    // 발신자 ID (회원 ID)
    @Column(nullable = false, columnDefinition = "VARCHAR(36)")
    private UUID senderId;

    @Column(nullable = false)
    private LocalDateTime timestamp;

    @ElementCollection(fetch=FetchType.EAGER)
    private List<Attachment> attachments;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="chat_room_id", nullable = false)
    private ChatRoom chatRoom;
}