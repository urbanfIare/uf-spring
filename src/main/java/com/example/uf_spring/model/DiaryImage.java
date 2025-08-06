package com.example.uf_spring.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "diary_images")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DiaryImage {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String originalFileName; // 원본 파일명
    
    @Column(nullable = false)
    private String storedFileName; // 저장된 파일명
    
    @Column(nullable = false)
    private String filePath; // 파일 경로
    
    @Column(nullable = false)
    private Long fileSize; // 파일 크기
    
    @Column(nullable = false)
    private String contentType; // 파일 타입 (image/jpeg, image/png 등)
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "diary_id", nullable = false)
    private Diary diary;
    
    @Column(nullable = false)
    private LocalDateTime uploadedAt;
    
    @Column(nullable = false)
    private boolean isDeleted = false;
    
    // 생성자
    public DiaryImage(String originalFileName, String storedFileName, String filePath, 
                     Long fileSize, String contentType, Diary diary) {
        this.originalFileName = originalFileName;
        this.storedFileName = storedFileName;
        this.filePath = filePath;
        this.fileSize = fileSize;
        this.contentType = contentType;
        this.diary = diary;
        this.uploadedAt = LocalDateTime.now();
    }
} 