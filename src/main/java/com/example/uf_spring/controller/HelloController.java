package com.example.uf_spring.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        return "안녕하세요! Spring Boot 서버가 정상적으로 작동하고 있습니다!";
    }

    @GetMapping("/")
    public String home() {
        return "Spring Boot 서버에 오신 것을 환영합니다!";
    }
} 