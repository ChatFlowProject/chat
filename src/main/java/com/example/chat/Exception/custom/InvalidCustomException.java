package com.example.chat.Exception.custom;

import com.example.chat.common.BaseResponseStatus;
import lombok.Getter;

@Getter
public class InvalidCustomException extends RuntimeException {
    private final BaseResponseStatus status;

    public InvalidCustomException(BaseResponseStatus status) {
        this.status = status;
    }
}
