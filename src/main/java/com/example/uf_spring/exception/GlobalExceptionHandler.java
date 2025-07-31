package com.example.uf_spring.exception;

import com.example.uf_spring.dto.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    // 일반적인 RuntimeException 처리
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Object>> handleRuntimeException(RuntimeException ex, WebRequest request) {
        log.error("RuntimeException 발생: {}", ex.getMessage(), ex);
        
        ApiResponse<Object> response = ApiResponse.error(ex.getMessage(), "RUNTIME_ERROR");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // IllegalArgumentException 처리
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalArgumentException(IllegalArgumentException ex, WebRequest request) {
        log.error("IllegalArgumentException 발생: {}", ex.getMessage(), ex);
        
        ApiResponse<Object> response = ApiResponse.error("잘못된 요청: " + ex.getMessage(), "INVALID_ARGUMENT");
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 일반적인 Exception 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception ex, WebRequest request) {
        log.error("예상치 못한 오류 발생: {}", ex.getMessage(), ex);
        
        ApiResponse<Object> response = ApiResponse.error("서버 내부 오류가 발생했습니다.", "INTERNAL_ERROR");
        
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }
} 