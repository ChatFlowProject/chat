package com.example.chatrepo.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {
    // FeignClient를 통해 헤더를 전달하는 함수이다. Header를 통해 들어온 JwtToken을 FeignClient의 Header로 넘겨준다.
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();

            // requestAttributes가 null이 아니고, ServletRequestAttributes 인스턴스인지 확인
            if (!(requestAttributes instanceof ServletRequestAttributes)) {
                return; // 요청 컨텍스트가 없으면 아무 작업도 수행하지 않음
            }

            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            if (request == null) {
                return; // request 자체가 null이면 처리 안 함
            }

            String token = request.getHeader(HttpHeaders.AUTHORIZATION);

            // 토큰이 null이 아니고, 공백 문자열이 아닐 경우에만 추가
            if (token != null && !token.trim().isEmpty()) {
                template.header(HttpHeaders.AUTHORIZATION, token);
            }
        };
    }
    // FeignErrorDecorder 사용을 위한 빈을 정의한다.
    @Bean
    public FeignErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}
