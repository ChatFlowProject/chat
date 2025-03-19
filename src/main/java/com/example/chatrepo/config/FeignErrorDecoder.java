package com.example.chatrepo.config;

import com.example.chatrepo.common.BaseException;
import com.example.chatrepo.common.BaseResponseStatus;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        HttpStatus status = HttpStatus.valueOf(response.status());

        switch (status) {
            case UNAUTHORIZED: // 401
                return new ResponseStatusException(status, "인증에 실패했습니다.");
            case FORBIDDEN: // 403
                return new ResponseStatusException(status, "접근 권한이 없습니다.");
            case NOT_FOUND: // 404
                return new ResponseStatusException(status, "요청한 리소스를 찾을 수 없습니다.");
            case INTERNAL_SERVER_ERROR: // 500
                return new ResponseStatusException(status, "서버 내부 오류가 발생했습니다.");
            default:
                return new ResponseStatusException(status, "알 수 없는 오류가 발생했습니다.");
        }
    }
}