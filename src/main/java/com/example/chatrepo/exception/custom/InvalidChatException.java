package com.example.chatrepo.exception.custom;

import com.example.chatrepo.common.BaseResponseStatus;

public class InvalidChatException extends InvalidCustomException {
    public InvalidChatException(BaseResponseStatus status) {
        super(status);
    }
}
