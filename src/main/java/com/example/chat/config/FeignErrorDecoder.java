package com.example.chat.config;

import com.example.chat.common.BaseException;
import com.example.chat.common.BaseResponseStatus;
import feign.Response;
import feign.codec.ErrorDecoder;
import org.springframework.stereotype.Component;

@Component
public class FeignErrorDecoder implements ErrorDecoder {
    public Exception decode(String methodKey, Response response) {
        int status = response.status();
        if(status == 403) {
            return new BaseException(BaseResponseStatus.UNAUTHORIZED_CLIENT);
        } else if(status == 401) {
            return new BaseException(BaseResponseStatus.UNAUTHORIZED_CLIENT);
        } else if(status == 400) {
            return new BaseException(BaseResponseStatus.BAD_ACCESS_TOKEN);
        }

        return new Exception(response.reason());
    }
}
/* ErrorDecorder로 FeignClient 에러를 핸들링하지 않으면 에러가 발생한 경우 서버 에러로 처리되어 넘어간다.
이렇게 되면 클라이언트에서 에러 상황을 제대로 파악하지 못하기 때문에 FeignClient를 통해 넘어온 에러를 커스텀하여 반환해주는 것이 좋다.
*/