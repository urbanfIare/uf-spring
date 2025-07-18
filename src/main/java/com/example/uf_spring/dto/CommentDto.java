package com.example.uf_spring.dto;

import java.time.LocalDateTime;

public class CommentDto {
    
    // 요청용 DTO
    public static class CreateRequest {
        private String content;
        
        public CreateRequest() {}
        
        public CreateRequest(String content) {
            this.content = content;
        }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    public static class UpdateRequest {
        private String content;
        
        public UpdateRequest() {}
        
        public UpdateRequest(String content) {
            this.content = content;
        }
        
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
    }
    
    // 응답용 DTO
    public static class Response {
        private Long id;
        private String content;
        private String authorName;
        private Long authorId;
        private Long postId;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        
        public Response() {}
        
        public Response(Long id, String content, String authorName, Long authorId, Long postId, LocalDateTime createdAt, LocalDateTime updatedAt) {
            this.id = id;
            this.content = content;
            this.authorName = authorName;
            this.authorId = authorId;
            this.postId = postId;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
        }
        
        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getAuthorName() { return authorName; }
        public void setAuthorName(String authorName) { this.authorName = authorName; }
        public Long getAuthorId() { return authorId; }
        public void setAuthorId(Long authorId) { this.authorId = authorId; }
        public Long getPostId() { return postId; }
        public void setPostId(Long postId) { this.postId = postId; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    }
} 