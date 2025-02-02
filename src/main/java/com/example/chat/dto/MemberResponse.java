package com.example.chat.dto;

import com.example.chat.common.MemberType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MemberResponse {
    private UUID id;         // Primary Key
    private String nickname;
    private String email;    // 이메일
    private String name;
    private String birth;
    private MemberType type;
    private LocalDateTime createdAt;
}

