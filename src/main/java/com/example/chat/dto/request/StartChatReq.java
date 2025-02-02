package com.example.chat.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class StartChatReq {
    @NotNull(message="상대방ID는 필수입니다.")
    private List<String> recipientIds;
}
