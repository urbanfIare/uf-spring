package com.example.uf_spring.controller;

import com.example.uf_spring.config.JwtConfig;
import com.example.uf_spring.dto.LoginRequest;
import com.example.uf_spring.dto.RegisterRequest;
import com.example.uf_spring.model.User;
import com.example.uf_spring.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "인증 관리", description = "회원가입, 로그인, 인증 상태 확인 API")
@Slf4j
public class AuthController {

    private final AuthService authService;
    private final JwtConfig jwtConfig;

    // 회원가입
    @PostMapping("/register")
    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "회원가입 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<?> register(
            @Parameter(description = "회원가입 정보", required = true) @RequestBody RegisterRequest request) {
        log.info("회원가입 요청 받음: {}", request.getEmail());
        try {
            User newUser = authService.register(request);
            log.info("회원가입 성공: {}", newUser.getEmail());
            
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
            log.error("회원가입 실패: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // JWT 로그인
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "로그인 성공", 
                    content = @Content(schema = @Schema(implementation = Map.class))),
        @ApiResponse(responseCode = "400", description = "잘못된 로그인 정보")
    })
    public ResponseEntity<?> login(
            @Parameter(description = "로그인 정보", required = true) @RequestBody LoginRequest request) {
        log.info("로그인 요청 받음: {}", request.getEmail());
        try {
            User user = authService.authenticate(request.getEmail(), request.getPassword());
            log.info("로그인 성공: {}", user.getEmail());
            
            // JWT 토큰 생성
            String token = jwtConfig.generateToken(user.getEmail(), user.getRole().name());
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "로그인이 완료되었습니다!");
            response.put("token", token);
            response.put("user", Map.of(
                "id", user.getId(),
                "name", user.getName(),
                "email", user.getEmail(),
                "age", user.getAge(),
                "role", user.getRole()
            ));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            log.error("로그인 실패: {} - {}", request.getEmail(), e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // 로그인 상태 확인
    @GetMapping("/status")
    @Operation(summary = "인증 상태 확인", description = "인증 API의 작동 상태를 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "인증 API 정상 작동")
    })
    public ResponseEntity<?> getAuthStatus() {
        log.info("인증 상태 확인 요청");
        return ResponseEntity.ok(Map.of("message", "인증 API가 정상적으로 작동합니다!"));
    }

    // 테스트용 관리자 계정 생성
    @PostMapping("/create-test-admin")
    @Operation(summary = "테스트용 관리자 계정 생성", description = "테스트를 위한 관리자 계정을 생성합니다.")
    public ResponseEntity<?> createTestAdmin() {
        try {
            RegisterRequest adminRequest = new RegisterRequest();
            adminRequest.setName("관리자");
            adminRequest.setEmail("admin");
            adminRequest.setPassword("admin");
            adminRequest.setAge(30);
            
            User adminUser = authService.register(adminRequest);
            
            Map<String, Object> response = new HashMap<>();
            response.put("message", "테스트용 관리자 계정이 생성되었습니다!");
            response.put("user", Map.of(
                "id", adminUser.getId(),
                "name", adminUser.getName(),
                "email", adminUser.getEmail(),
                "role", adminUser.getRole()
            ));
            response.put("login_info", Map.of(
                "email", "admin",
                "password", "admin"
            ));
            
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
} 