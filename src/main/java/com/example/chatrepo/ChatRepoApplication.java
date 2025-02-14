package com.example.chatrepo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients(basePackages = "com.example.chatrepo.config") // Feign Client 패키지 경로 지정
public class ChatRepoApplication {

    public static void main(String[] args) {
        SpringApplication.run(ChatRepoApplication.class, args);
    }

}
