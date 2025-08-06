package com.example.uf_spring;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordHashTest {

    @Test
    public void testPasswordHash() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 현재 사용 중인 해시값
        String currentHash = "$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVEFDa";
        
        // 테스트할 비밀번호들
        String[] testPasswords = {"admin", "user", "test1", "password", "123456"};
        
        System.out.println("=== 비밀번호 해시 테스트 ===");
        System.out.println("현재 해시값: " + currentHash);
        System.out.println();
        
        for (String password : testPasswords) {
            boolean matches = encoder.matches(password, currentHash);
            System.out.println("비밀번호 '" + password + "' 일치: " + matches);
        }
        
        System.out.println();
        System.out.println("=== 새로운 해시 생성 ===");
        for (String password : testPasswords) {
            String newHash = encoder.encode(password);
            System.out.println("'" + password + "' -> " + newHash);
        }
    }
} 