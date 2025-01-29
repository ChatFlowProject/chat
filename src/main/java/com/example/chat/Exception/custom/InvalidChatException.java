package com.example.chat.Exception.custom;

import com.example.chat.common.BaseResponseStatus;

public class InvalidChatException extends InvalidCustomException {
    public InvalidChatException(BaseResponseStatus status) {
        super(status);
    }
}
