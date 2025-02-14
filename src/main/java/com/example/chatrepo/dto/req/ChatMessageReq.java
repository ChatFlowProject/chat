package com.example.chatrepo.dto.req;

import com.example.chatrepo.dto.Attachment;
import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageReq {
    private UUID senderId; // 발신자 ID
    private String message; // 메시지 내용
    private List<Attachment> attachments; // 첨부 파일 리스트 (선택 사항)
}
