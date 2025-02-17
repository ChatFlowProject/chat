package com.example.chatrepo.dto.res;

import com.example.chatrepo.dto.Attachment;
import com.example.chatrepo.dto.Sender;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
@Getter
public class GetMessageRes {
    private Long messageId;
    private Long chatRoomId;
    private List<Sender> sender;
    private String message; // 메시지 내용
    private List<Attachment> attachments; // 첨부 파일 리스트 (선택 사항)
    private LocalDateTime timestamp;
    private List<Long> mentionedUserIds; // 멘션된 사용자 ID 리스트
}
