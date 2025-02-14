package com.example.chatrepo.config;

import com.example.chatrepo.common.BaseException;
import com.example.chatrepo.common.BaseResponseStatus;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {

    @Override
    public Exception decode(String methodKey, Response response) {
        switch (response.status()) {
            case 400:
                return new BaseException(BaseResponseStatus.FAIL);
            case 404:
                return new BaseException(BaseResponseStatus.FAIL);
            case 500:
                return new BaseException(BaseResponseStatus.FAIL);
            default:
                return new Exception("Feign Client 에러: " + response.reason());
        }
    }
}