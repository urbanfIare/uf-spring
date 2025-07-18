package com.example.uf_spring.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Post {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String title;
    
    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private int viewCount = 0;
    
    @Column(nullable = false)
    private int likeCount = 0;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PostCategory category = PostCategory.GENERAL;
    
    @Column(nullable = false)
    private boolean isDeleted = false;
    
    // 생성자
    public Post(String title, String content, User author, PostCategory category) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.category = category;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // 조회수 증가
    public void incrementViewCount() {
        this.viewCount++;
    }
    
    // 좋아요 증가
    public void incrementLikeCount() {
        this.likeCount++;
    }
    
    // 좋아요 감소
    public void decrementLikeCount() {
        if (this.likeCount > 0) {
            this.likeCount--;
        }
    }
    
    // 수정 시간 업데이트
    public void updateModifiedTime() {
        this.updatedAt = LocalDateTime.now();
    }
} 