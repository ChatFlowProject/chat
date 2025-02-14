package com.example.chatrepo.dto.res;

import com.example.chatrepo.dto.Attachment;
import com.example.chatrepo.dto.Sender;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChatMessageRes {
    private Long messageId;
    private Long chatRoomId;
    private Sender sender;
    private String message;
    private List<Attachment> attachments;
    private LocalDateTime timestamp;
    private String status;
}
