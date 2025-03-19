package com.example.chatrepo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Participant {
    private UUID userId; // 참여자 ID
    private String nickname; // 닉네임
//    private String avatarUrl; // 프로필 이미지 URL
}


