package com.example.chat.Exception.custom;

import com.example.chat.common.BaseResponseStatus;

public class InvalidFileException extends InvalidCustomException {

    public InvalidFileException(BaseResponseStatus status) {
        super(status);
    }
}
