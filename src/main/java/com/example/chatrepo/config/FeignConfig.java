package com.example.chatrepo.config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Value("${feign.client.member-service.token}")
    private String token;

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            // application.yml에서 읽어온 Bearer 토큰 추가
            requestTemplate.header("Authorization", "Bearer " + token);
        };
    }
}
