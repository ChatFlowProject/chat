package com.example.chatrepo.exception.custom;

import com.example.chatrepo.common.BaseResponseStatus;
import lombok.Getter;

@Getter
public class InvalidCustomException extends RuntimeException {
    private final BaseResponseStatus status;

    public InvalidCustomException(BaseResponseStatus status) {
        this.status = status;
    }
}
