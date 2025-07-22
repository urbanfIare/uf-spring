package com.example.uf_spring.dto;

import com.example.uf_spring.model.PostCategory;
import java.time.LocalDateTime;
import java.util.List;

public class PostDto {
    
    // 요청용 DTO
    public static class CreateRequest {
        private String title;
        private String content;
        private PostCategory category;
        
        public CreateRequest() {}
        
        public CreateRequest(String title, String content, PostCategory category) {
            this.title = title;
            this.content = content;
            this.category = category;
        }
        
        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public PostCategory getCategory() { return category; }
        public void setCategory(PostCategory category) { this.category = category; }
    }
    
    public static class UpdateRequest {
        private String title;
        private String content;
        private PostCategory category;
        
        public UpdateRequest() {}
        
        public UpdateRequest(String title, String content, PostCategory category) {
            this.title = title;
            this.content = content;
            this.category = category;
        }
        
        // getters and setters
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public PostCategory getCategory() { return category; }
        public void setCategory(PostCategory category) { this.category = category; }
    }
    
    // 응답용 DTO
    public static class Response {
        private Long id;
        private String title;
        private String content;
        private String authorName;
        private Long authorId;
        private PostCategory category;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
        private int viewCount;
        private int likeCount;
        private int commentCount;
        
        public Response() {}
        
        public Response(Long id, String title, String content, String authorName, Long authorId, 
                      PostCategory category, LocalDateTime createdAt, LocalDateTime updatedAt, 
                      int viewCount, int likeCount, int commentCount) {
            this.id = id;
            this.title = title;
            this.content = content;
            this.authorName = authorName;
            this.authorId = authorId;
            this.category = category;
            this.createdAt = createdAt;
            this.updatedAt = updatedAt;
            this.viewCount = viewCount;
            this.likeCount = likeCount;
            this.commentCount = commentCount;
        }
        
        // getters and setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getContent() { return content; }
        public void setContent(String content) { this.content = content; }
        public String getAuthorName() { return authorName; }
        public void setAuthorName(String authorName) { this.authorName = authorName; }
        public Long getAuthorId() { return authorId; }
        public void setAuthorId(Long authorId) { this.authorId = authorId; }
        public PostCategory getCategory() { return category; }
        public void setCategory(PostCategory category) { this.category = category; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getUpdatedAt() { return updatedAt; }
        public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
        public int getViewCount() { return viewCount; }
        public void setViewCount(int viewCount) { this.viewCount = viewCount; }
        public int getLikeCount() { return likeCount; }
        public void setLikeCount(int likeCount) { this.likeCount = likeCount; }
        public int getCommentCount() { return commentCount; }
        public void setCommentCount(int commentCount) { this.commentCount = commentCount; }
    }
    
    // 검색 및 정렬을 위한 요청 DTO
    public static class SearchRequest {
        private String keyword;
        private PostCategory category;
        private String sortBy; // "latest", "popular", "mostLiked", "mostViewed"
        private String sortOrder; // "asc", "desc"
        private int page = 0;
        private int size = 10;
        
        public SearchRequest() {}
        
        public SearchRequest(String keyword, PostCategory category, String sortBy, String sortOrder, int page, int size) {
            this.keyword = keyword;
            this.category = category;
            this.sortBy = sortBy;
            this.sortOrder = sortOrder;
            this.page = page;
            this.size = size;
        }
        
        // getters and setters
        public String getKeyword() { return keyword; }
        public void setKeyword(String keyword) { this.keyword = keyword; }
        public PostCategory getCategory() { return category; }
        public void setCategory(PostCategory category) { this.category = category; }
        public String getSortBy() { return sortBy; }
        public void setSortBy(String sortBy) { this.sortBy = sortBy; }
        public String getSortOrder() { return sortOrder; }
        public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
    }
    
    // 페이징 응답 DTO
    public static class PageResponse {
        private List<Response> content;
        private int page;
        private int size;
        private long totalElements;
        private int totalPages;
        
        public PageResponse() {}
        
        public PageResponse(List<Response> content, int page, int size, long totalElements, int totalPages) {
            this.content = content;
            this.page = page;
            this.size = size;
            this.totalElements = totalElements;
            this.totalPages = totalPages;
        }
        
        // getters and setters
        public List<Response> getContent() { return content; }
        public void setContent(List<Response> content) { this.content = content; }
        public int getPage() { return page; }
        public void setPage(int page) { this.page = page; }
        public int getSize() { return size; }
        public void setSize(int size) { this.size = size; }
        public long getTotalElements() { return totalElements; }
        public void setTotalElements(long totalElements) { this.totalElements = totalElements; }
        public int getTotalPages() { return totalPages; }
        public void setTotalPages(int totalPages) { this.totalPages = totalPages; }
    }
} 