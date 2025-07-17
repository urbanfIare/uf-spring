package com.example.uf_spring.config;

import com.example.uf_spring.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UserService userService;

    @Override
    public void run(String... args) throws Exception {
        // 애플리케이션 시작 시 샘플 데이터 초기화
        userService.initializeSampleData();
        System.out.println("✅ 샘플 데이터가 초기화되었습니다!");
    }
} 