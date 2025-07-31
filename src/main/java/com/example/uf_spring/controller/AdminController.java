package com.example.uf_spring.controller;

import com.example.uf_spring.model.User;
import com.example.uf_spring.model.Role;
import com.example.uf_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 관리자 계정 생성 (개발용)
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdminAccount() {
        try {
            // 이미 admin 계정이 있는지 확인
            if (userRepository.findByEmail("admin").isPresent()) {
                return ResponseEntity.badRequest().body(Map.of(
                    "error", "admin 계정이 이미 존재합니다.",
                    "message", "이메일: admin, 비밀번호: admin"
                ));
            }

            // BCrypt로 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode("admin");

            // admin 계정 생성
            User adminUser = new User("관리자", "admin", encodedPassword, 30, Role.ADMIN);
            User savedUser = userRepository.save(adminUser);

            Map<String, Object> response = new HashMap<>();
            response.put("message", "관리자 계정이 성공적으로 생성되었습니다!");
            response.put("user", Map.of(
                "id", savedUser.getId(),
                "name", savedUser.getName(),
                "email", savedUser.getEmail(),
                "role", savedUser.getRole()
            ));
            response.put("login_info", Map.of(
                "email", "admin",
                "password", "admin"
            ));

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "관리자 계정 생성 실패: " + e.getMessage()
            ));
        }
    }

    // 현재 사용자 목록 조회
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            var users = userRepository.findAll();
            return ResponseEntity.ok(Map.of(
                "total_count", users.size(),
                "users", users
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "사용자 목록 조회 실패: " + e.getMessage()
            ));
        }
    }

    // 데이터베이스 초기화 상태 확인
    @GetMapping("/init-status")
    public ResponseEntity<?> getInitStatus() {
        try {
            long userCount = userRepository.count();
            boolean hasAdmin = userRepository.findByEmail("admin").isPresent();
            
            Map<String, Object> response = new HashMap<>();
            response.put("total_users", userCount);
            response.put("has_admin", hasAdmin);
            response.put("message", hasAdmin ? 
                "관리자 계정이 존재합니다. (admin/admin)" : 
                "관리자 계정이 없습니다. /api/admin/create-admin을 호출하여 생성하세요.");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", "초기화 상태 확인 실패: " + e.getMessage()
            ));
        }
    }
} 