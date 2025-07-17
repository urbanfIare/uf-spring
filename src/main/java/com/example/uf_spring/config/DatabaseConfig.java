package com.example.uf_spring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

import jakarta.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;

@Configuration
public class DatabaseConfig {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String username;

    @Value("${spring.datasource.password}")
    private String password;

    @PostConstruct
    public void initializeDatabase() {
        try {
            // 데이터베이스 파일 경로 추출
            String filePath = extractFilePathFromUrl(databaseUrl);
            File dbFile = new File(filePath);
            
            // 데이터 디렉토리 생성
            File dataDir = dbFile.getParentFile();
            if (!dataDir.exists()) {
                dataDir.mkdirs();
                System.out.println("📁 데이터베이스 디렉토리 생성: " + dataDir.getAbsolutePath());
            }

            System.out.println("🗄️ 데이터베이스 파일 경로: " + dbFile.getAbsolutePath());
            System.out.println("👤 사용자명: " + username);
            System.out.println("🔗 연결 URL: " + databaseUrl);
            
        } catch (Exception e) {
            System.err.println("❌ 데이터베이스 초기화 오류: " + e.getMessage());
        }
    }

    private String extractFilePathFromUrl(String url) {
        // jdbc:h2:file:./data/uf_spring_db 에서 파일 경로 추출
        if (url.startsWith("jdbc:h2:file:")) {
            return url.substring("jdbc:h2:file:".length());
        }
        return "./data/uf_spring_db";
    }

    // 데이터베이스 정보 조회 메서드
    public String getDatabaseInfo() {
        return String.format("""
            📊 데이터베이스 정보:
            - 파일 경로: %s
            - 사용자명: %s
            - 연결 URL: %s
            """, 
            extractFilePathFromUrl(databaseUrl),
            username,
            databaseUrl
        );
    }
} 