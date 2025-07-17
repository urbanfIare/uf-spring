package com.example.uf_spring.controller;

import com.example.uf_spring.dto.LoginRequest;
import com.example.uf_spring.dto.RegisterRequest;
import com.example.uf_spring.model.User;
import com.example.uf_spring.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    // 회원가입
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            User newUser = authService.register(request);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "회원가입이 완료되었습니다!");
            response.put("user", Map.of(
                "id", newUser.getId(),
                "name", newUser.getName(),
                "email", newUser.getEmail(),
                "age", newUser.getAge(),
                "role", newUser.getRole()
            ));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 로그인 상태 확인
    @GetMapping("/status")
    public ResponseEntity<?> getAuthStatus() {
        return ResponseEntity.ok(Map.of("message", "인증 API가 정상적으로 작동합니다!"));
    }
} 