package com.example.chatrepo.common;

import lombok.Getter;

@Getter
public enum BaseResponseStatus {
    // 모든 요청 성공 1000
    SUCCESS(true, 1000, "요청이 성공하였습니다."),
    FAIL(false, 1100, "요청이 실패하였습니다."),
    USER_NOT_FOUND(false, 1001, "회원이 없습니다"),

    // 채팅 기능 - 8000
    CHAT_INVALID_CHATROOM_ID(false, 8011, "채팅방을 조회할 수 없습니다."),
    CHAT_INVALID_USER_ID(false, 8012, "채팅방에 참여하지 않는 사용자 입니다."),
    CHAT_SELF_CHAT(false, 8013, "자신과 채팅을 진행할 수 없습니다."),
    CHAT_SERVER_ERROR(false,8014,"채팅 서버에 문제가 있습니다."),
    CHAT_ROOM_CREATION_FAILED(false, 8015, "채팅방을 만든는데에 실패했습니다."),
    CHAT_ROOM_FETCH_FAILED(false, 8016, "채팅방 목록을 불러오는데 실패했습니다."),
    CHAT_MESSAGE_FETCH_FAILED(false, 8017, "메시지를 불러오는데 실패했습니다."),
    CHAT_MESSAGE_DELETE_FAILED(false, 8018, "체팅 메시지를 삭제하는 데 실패했습니다."),
    CHAT_ROOM_DELETE_FAILED(false, 8019, "채팅방를 삭제하는데 실패했습니다."),
    CHAT_MESSAGE_NOT_FOUND(false, 8020, "해당 메세지를 찾을 수 없습니다."),
    CHAT_ROOM_USER_NOT(false, 8021, "해당 채팅방에 해당 사용자가 없습니다."),


    // 유저 기능 - 2000
    UNAUTHORIZED_CLIENT(false, 2000, "인증되지 않은 클라이언트입니다."),
    BAD_ACCESS_TOKEN(false, 2001, "잘못 접근한 토큰입니다."),
    CHAT_INVALID_PARTICIPANTS(false, 2092, "유효하지 않는 참여자입니다."),

    // 파일 기능 - 3000
    EXCEED_MAX_SIZE(false, 3001, "파일의 크기가 큽니다."),
    INVALID_FILE_TYLE(false, 3002, "파일의 형식이 유효하지 않습니다."),

    // 서버 에러 - 4000
    INTERNAL_SERVER_ERROR1(false,4001,"서버에러1" ),
    INTERNAL_SERVER_ERROR2(false,4002,"서버에러2"),
    INTERNAL_SERVER_ERROR3(false, 4003, "서버에러3");

    private final boolean isSuccess;
    private final Integer code;
    private final String message;
    BaseResponseStatus(Boolean isSuccess, Integer code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

