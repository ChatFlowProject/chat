package com.example.chatrepo.config.member_server;

import com.example.chatrepo.common.ApiResponse;
import com.example.chatrepo.common.MemberResponse;
import com.example.chatrepo.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@FeignClient(
        name = "member-service",
        url = "http://flowchat.shop:30002", // 실제 멤버 서비스 경로
        configuration = FeignConfig.class
)
public interface MemberServiceClient {

    // 친구 목록 API 호출
    @GetMapping("/members/friends") // 멤버 서비스의 실제 경로와 일치해야 함
    ApiResponse<List<MemberResponse>> getAllMembers();

}
