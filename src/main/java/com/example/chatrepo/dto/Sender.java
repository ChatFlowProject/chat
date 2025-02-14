package com.example.chatrepo.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Sender {
    private UUID userId;
    private String username;
    private String avatarUrl;
}
