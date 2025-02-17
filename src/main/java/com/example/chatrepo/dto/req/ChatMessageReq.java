package com.example.chatrepo.dto.req;

import com.example.chatrepo.dto.Attachment;
import com.example.chatrepo.dto.Sender;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageReq {
    private UUID userId;
    private String message; // 메시지 내용
    private List<Attachment> attachments; // 첨부 파일 리스트 (선택 사항)
    private List<Long> mentionedUserIds; // 멘션된 사용자 ID 리스트
}
