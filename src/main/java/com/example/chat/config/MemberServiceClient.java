package com.example.chat.config;

import com.example.chat.dto.MemberResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "member-service", configuration = FeignConfig.class)
public interface MemberServiceClient {
    @GetMapping("/members/{memberId}")
    ResponseEntity<MemberResponse> getMemberByMemberId(@PathVariable("memberId") String memberId);
}

