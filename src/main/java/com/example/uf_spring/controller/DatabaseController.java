package com.example.uf_spring.controller;

import com.example.uf_spring.config.DatabaseConfig;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;

@RestController
@RequestMapping("/api/database")
@RequiredArgsConstructor
public class DatabaseController {

    private final DatabaseConfig databaseConfig;

    @GetMapping("/info")
    public String getDatabaseInfo() {
        return databaseConfig.getDatabaseInfo();
    }

    @GetMapping("/status")
    public String getDatabaseStatus() {
        try {
            // 데이터베이스 파일 존재 여부 확인
            String dbPath = "./data/uf_spring_db.mv.db";
            File dbFile = new File(dbPath);
            
            if (dbFile.exists()) {
                long fileSize = dbFile.length();
                return String.format("""
                    ✅ 데이터베이스 상태:
                    - 파일 존재: 예
                    - 파일 크기: %d bytes
                    - 파일 경로: %s
                    """, fileSize, dbFile.getAbsolutePath());
            } else {
                return String.format("""
                    ⚠️ 데이터베이스 상태:
                    - 파일 존재: 아니오
                    - 파일 경로: %s
                    - 첫 실행 시 자동 생성됩니다.
                    """, dbFile.getAbsolutePath());
            }
        } catch (Exception e) {
            return "❌ 데이터베이스 상태 확인 오류: " + e.getMessage();
        }
    }
} 