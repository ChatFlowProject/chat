package com.example.chat.dto;

import lombok.Data;

@Data
public class MemberResponse {
    private Long id;         // Primary Key
    private String memberId; // 사용자 고유 ID
    private String email;    // 이메일
    private String name;     // 이름
    private String memberState; // 상태 (예: ACTIVE, INACTIVE)
}

