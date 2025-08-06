package com.example.uf_spring.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "diaries")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Diary {
    
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
    
    // 일기 특화 필드들
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Weather weather = Weather.SUNNY; // 날씨
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Mood mood = Mood.NORMAL; // 기분
    
    @Column(nullable = false)
    private boolean isPrivate = true; // 비공개 여부
    
    @Column(nullable = false)
    private boolean isDeleted = false;
    
    // 사진 첨부 (일기와 1:N 관계)
    @OneToMany(mappedBy = "diary", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<DiaryImage> images;
    
    // 태그 (일기와 N:M 관계)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "diary_tags",
        joinColumns = @JoinColumn(name = "diary_id"),
        inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tags;
    
    // 생성자
    public Diary(String title, String content, User author, Weather weather, Mood mood) {
        this.title = title;
        this.content = content;
        this.author = author;
        this.weather = weather;
        this.mood = mood;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // 수정 시간 업데이트
    public void updateModifiedTime() {
        this.updatedAt = LocalDateTime.now();
    }
    
    // 날씨 열거형
    public enum Weather {
        SUNNY("맑음"),
        CLOUDY("흐림"),
        RAINY("비"),
        SNOWY("눈"),
        WINDY("바람");
        
        private final String description;
        
        Weather(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
    
    // 기분 열거형
    public enum Mood {
        VERY_HAPPY("매우 좋음"),
        HAPPY("좋음"),
        NORMAL("보통"),
        SAD("슬픔"),
        VERY_SAD("매우 슬픔"),
        ANGRY("화남"),
        EXCITED("신남");
        
        private final String description;
        
        Mood(String description) {
            this.description = description;
        }
        
        public String getDescription() {
            return description;
        }
    }
} 