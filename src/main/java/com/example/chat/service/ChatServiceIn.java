package com.example.chat.service;

import com.example.chat.dto.MemberResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.security.core.userdetails.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "member", url = "https://dashboard.flowchat.shop:30001")
public interface ChatServiceIn {
    @GetMapping("/users/{username}")
    MemberResponse getMemberByUsername(@PathVariable("username") String username);
}
