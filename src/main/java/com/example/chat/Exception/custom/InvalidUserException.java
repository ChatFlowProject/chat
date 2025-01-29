package com.example.chat.Exception.custom;

import com.example.chat.common.BaseResponseStatus;

public class InvalidUserException extends InvalidCustomException {
    public InvalidUserException(BaseResponseStatus status) {
        super(status);
    }
}
