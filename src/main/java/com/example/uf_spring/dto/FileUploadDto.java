package com.example.uf_spring.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

public class FileUploadDto {
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String originalFileName;
        private String storedFileName;
        private String filePath;
        private String fileType;
        private Long fileSize;
        private LocalDateTime uploadedAt;
        private Long postId;
        private Long userId;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private String originalFileName;
        private String storedFileName;
        private String filePath;
        private String fileType;
        private Long fileSize;
        private Long postId;
        private Long userId;
    }
    
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UpdateRequest {
        private String originalFileName;
        private String filePath;
    }
} 