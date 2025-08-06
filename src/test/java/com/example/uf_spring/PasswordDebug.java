package com.example.uf_spring;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordDebug {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 현재 사용 중인 해시값
        String currentHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa";
        
        System.out.println("=== 현재 해시값 분석 ===");
        System.out.println("해시값: " + currentHash);
        System.out.println();
        
        // 다양한 비밀번호로 테스트
        String[] testPasswords = {
            "admin", "user", "password", "123456", "test", 
            "admin123", "user123", "password123", "test123",
            "a", "b", "c", "1", "2", "3"
        };
        
        System.out.println("=== 비밀번호 매칭 테스트 ===");
        boolean found = false;
        for (String password : testPasswords) {
            boolean matches = encoder.matches(password, currentHash);
            System.out.println("'" + password + "' -> " + matches);
            if (matches) {
                System.out.println("✅ 일치하는 비밀번호 발견: '" + password + "'");
                found = true;
            }
        }
        
        if (!found) {
            System.out.println("❌ 일치하는 비밀번호를 찾을 수 없습니다.");
        }
        
        System.out.println();
        System.out.println("=== 새로운 해시 생성 (admin/user용) ===");
        String adminHash = encoder.encode("admin");
        String userHash = encoder.encode("user");
        
        System.out.println("admin -> " + adminHash);
        System.out.println("user -> " + userHash);
        
        System.out.println();
        System.out.println("=== 새로운 해시 검증 ===");
        System.out.println("admin 매칭: " + encoder.matches("admin", adminHash));
        System.out.println("user 매칭: " + encoder.matches("user", userHash));
    }
} 