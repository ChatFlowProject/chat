package com.example.chat.common;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    // 모든 요청 성공 1000
    SUCCESS(true, 1000, "요청이 성공하였습니다."),
    USER_NOT_FOUND(false, 1001, "회원이 없습니다"),

    // 채팅 기능 - 8000
    CHAT_INVALID_CHATROOM_ID(false, 8011, "채팅방을 조회할 수 없습니다."),
    CHAT_INVALID_USER_ID(false, 8012, "채팅방에 참여하지 않는 사용자 입니다."),
    CHAT_SELF_CHAT(false, 8013, "자신과 채팅을 진행할 수 없습니다."),
    UNAUTHORIZED_CLIENT(false, 9000, "인증되지 않은 클라이언트입니다."),
    BAD_ACCESS_TOKEN(false, 9001, "잘못 접근한 토큰입니다."),
    CHAT_INVALID_PARTICIPANTS(false, 9092, "유효하지 않는 참여자입니다.");


    private final boolean isSuccess;
    private final Integer code;
    private final String message;
    BaseResponseStatus(Boolean isSuccess, Integer code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
