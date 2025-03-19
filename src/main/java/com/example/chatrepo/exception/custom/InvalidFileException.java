package com.example.chatrepo.exception.custom;

import com.example.chatrepo.common.BaseResponseStatus;

public class InvalidFileException extends InvalidCustomException {

    public InvalidFileException(BaseResponseStatus status) {
        super(status);
    }
}