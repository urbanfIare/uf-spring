package com.example.uf_spring.dto;

import com.example.uf_spring.model.Report.ReportStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

public class ReportDto {
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CreateRequest {
        private Long reporterId;
        private Long postId;
        private Long commentId;
        private String reason;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Response {
        private Long id;
        private String reporterName;
        private Long postId;
        private Long commentId;
        private String reason;
        private ReportStatus status;
        private LocalDateTime createdAt;
    }
} 