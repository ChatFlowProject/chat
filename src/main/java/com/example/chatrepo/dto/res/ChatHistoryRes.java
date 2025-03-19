package com.example.chatrepo.dto.res;

import com.example.chatrepo.dto.Attachment;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatHistoryRes {
    private Long chatRoomId; // 채팅방 ID
    private List<ChatMessageDetail> messages; // 메시지 상세 정보 리스트

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class ChatMessageDetail {
        private Long messageId; // 메시지 ID
        private UUID senderId; // 보낸 사람 ID
        private String senderName; // 보낸 사람 이름
        private String message; // 메시지 내용
        private LocalDateTime timestamp; // 메시지 전송 시간
        private List<Attachment> attachments; // 첨부 파일 리스트 (선택 사항)
    }
}

