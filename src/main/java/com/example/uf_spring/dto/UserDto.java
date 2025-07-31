package com.example.uf_spring.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.*;

public class UserDto {
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        @NotBlank(message = "이름은 필수입니다.")
        @Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다.")
        private String name;
        
        @NotBlank(message = "이메일은 필수입니다.")
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;
        
        @NotBlank(message = "비밀번호는 필수입니다.")
        @Size(min = 6, max = 100, message = "비밀번호는 6-100자 사이여야 합니다.")
        private String password;
        
        @NotNull(message = "나이는 필수입니다.")
        @Min(value = 1, message = "나이는 1세 이상이어야 합니다.")
        @Max(value = 150, message = "나이는 150세 이하여야 합니다.")
        private Integer age;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        @Size(min = 2, max = 50, message = "이름은 2-50자 사이여야 합니다.")
        private String name;
        
        @Email(message = "올바른 이메일 형식이 아닙니다.")
        private String email;
        
        @Size(min = 6, max = 100, message = "비밀번호는 6-100자 사이여야 합니다.")
        private String password;
        
        @Min(value = 1, message = "나이는 1세 이상이어야 합니다.")
        @Max(value = 150, message = "나이는 150세 이하여야 합니다.")
        private Integer age;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String name;
        private String email;
        private Integer age;
        private String role;
        private String createdAt;
    }
} 