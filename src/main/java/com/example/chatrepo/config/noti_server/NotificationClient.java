package com.example.chatrepo.config.noti_server;

import com.example.chatrepo.config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(
        name = "noti-service",
        url = "http://localhost:8089",
        configuration = FeignConfig.class
)
public interface NotificationClient {
    @PostMapping("/noti/send/mention")
    void sendMentionNotification(@RequestBody MentionNotificationRequest request);
}
