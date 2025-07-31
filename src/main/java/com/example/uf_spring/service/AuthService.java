package com.example.uf_spring.service;

import com.example.uf_spring.dto.LoginRequest;
import com.example.uf_spring.dto.RegisterRequest;
import com.example.uf_spring.model.User;
import com.example.uf_spring.model.Role;
import com.example.uf_spring.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    // 회원가입
    public User register(RegisterRequest request) {
        // 이메일 중복 확인
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("이미 존재하는 이메일입니다.");
        }

        // 비밀번호 암호화
        String encodedPassword = passwordEncoder.encode(request.getPassword());

        // 새 사용자 생성
        User newUser = new User(
            request.getName(),
            request.getEmail(),
            encodedPassword,
            request.getAge(),
            Role.USER // 기본 역할
        );

        return userRepository.save(newUser);
    }

    // 로그인 (이메일로 사용자 찾기)
    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    // 비밀번호 검증
    public boolean verifyPassword(String rawPassword, String encodedPassword) {
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

    // 사용자 인증 (로그인)
    public User authenticate(String email, String password) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));
        
        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new RuntimeException("비밀번호가 일치하지 않습니다.");
        }
        
        return user;
    }
} 