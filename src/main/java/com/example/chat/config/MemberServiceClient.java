package com.example.chat.config;

import com.example.chat.dto.MemberResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(name = "member-service", configuration = FeignConfig.class)
public interface MemberServiceClient {

    // 회원 전체 조회 API 호출
    @GetMapping("/admin/members")
    ResponseEntity<List<MemberResponse>> getAllMembers();
    // 특정 회원 정보 조회 API 호출
    @GetMapping("/admin/members/{memberId}")
    ResponseEntity<MemberResponse> getMemberById(@PathVariable("memberId") String memberId);
}



