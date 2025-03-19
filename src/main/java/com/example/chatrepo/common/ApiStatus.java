package com.example.chatrepo.common;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum ApiStatus {
    SUCCESS,
    ERROR;

    @JsonCreator
    public static ApiStatus fromValue(int value) {
        switch (value) {
            case 200:
                return SUCCESS;
            case 500:
                return ERROR;
            default:
                throw new IllegalArgumentException("Unknown status code: " + value);
        }
    }
}