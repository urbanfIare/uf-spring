package com.example.uf_spring;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class HashVerification {
    
    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 새로운 해시값
        String newHash = "$2a$10$dXJ3SW6G7P50lGmrMBSorO3v3rgoj1iZrxg3VWlizxnC9wQAYQi1G";
        
        System.out.println("=== 새로운 해시값 검증 ===");
        System.out.println("해시값: " + newHash);
        System.out.println();
        
        // 테스트할 비밀번호들
        String[] testPasswords = {"admin", "user", "password", "123456"};
        
        for (String password : testPasswords) {
            boolean matches = encoder.matches(password, newHash);
            System.out.println("'" + password + "' -> " + matches);
        }
        
        System.out.println();
        System.out.println("=== 새로운 해시 생성 ===");
        String adminHash = encoder.encode("admin");
        String userHash = encoder.encode("user");
        
        System.out.println("admin -> " + adminHash);
        System.out.println("user -> " + userHash);
        
        System.out.println();
        System.out.println("=== 검증 ===");
        System.out.println("admin 매칭: " + encoder.matches("admin", adminHash));
        System.out.println("user 매칭: " + encoder.matches("user", userHash));
    }
} 