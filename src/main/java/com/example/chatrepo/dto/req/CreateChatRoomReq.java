package com.example.chatrepo.dto.req;

import lombok.Getter;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class CreateChatRoomReq {
    private UUID userId;
    private List<UUID> recipientIds;
}
