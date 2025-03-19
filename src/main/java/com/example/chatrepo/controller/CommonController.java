package com.example.chatrepo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RequiredArgsConstructor
@RestController
@RequestMapping("/common")
public class CommonController {
    private final Environment env;

    @GetMapping("/health-check")
    public String status(){
        return String.format("working in chat service on port %s", env.getProperty("local.server.port"));
    }
}