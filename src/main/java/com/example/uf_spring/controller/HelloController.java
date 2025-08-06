package com.example.uf_spring.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@Slf4j
public class HelloController {

    @GetMapping("/hello")
    public String hello() {
        log.info("Hello API 호출됨");
        return "안녕하세요! Spring Boot 서버가 정상적으로 작동하고 있습니다!";
    }

    @GetMapping("/")
    public String home() {
        log.info("Home API 호출됨");
        return "Spring Boot 서버에 오신 것을 환영합니다!";
    }
} 