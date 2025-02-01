package com.example.chat.config;


import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.http.HttpHeaders;
import org.springframework.context.annotation.Bean;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class FeignConfig {
    // FeignClient를 통해 헤더를 전달하는 함수이다. Header를 통해 들어온 JwtToken을 FeignClient의 Header로 넘겨준다.
    @Bean
    public RequestInterceptor requestInterceptor() {
        return template -> {
            RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
            if (requestAttributes != null) {
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                String token = request.getHeader("Authorization");
                if (token != null && !token.isEmpty()) {
                    template.header(HttpHeaders.AUTHORIZATION, token);
                    System.out.println("Token added to header: " + token);
                } else {
                    System.out.println("Authorization header is missing or empty");
                }
            } else {
                System.out.println("No RequestAttributes found");
            }
        };
    }


    // FeignErrorDecorder 사용을 위한 빈을 정의한다.
    @Bean
    public FeignErrorDecoder errorDecoder() {
        return new FeignErrorDecoder();
    }
}
