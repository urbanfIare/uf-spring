package com.example.uf_spring.repository;

import com.example.uf_spring.model.Diary;
import com.example.uf_spring.model.Diary.Weather;
import com.example.uf_spring.model.Diary.Mood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Repository
public interface DiaryRepository extends JpaRepository<Diary, Long> {
    
    // 삭제되지 않은 일기만 조회
    List<Diary> findByIsDeletedFalseOrderByCreatedAtDesc();
    
    // 작성자별 일기 조회
    List<Diary> findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(Long authorId);
    
    // 제목으로 검색
    List<Diary> findByTitleContainingAndIsDeletedFalseOrderByCreatedAtDesc(String title);
    
    // 내용으로 검색
    List<Diary> findByContentContainingAndIsDeletedFalseOrderByCreatedAtDesc(String content);
    
    // 제목 또는 내용으로 검색
    @Query("SELECT d FROM Diary d WHERE (d.title LIKE %:keyword% OR d.content LIKE %:keyword%) AND d.isDeleted = false ORDER BY d.createdAt DESC")
    List<Diary> findByTitleOrContentContaining(@Param("keyword") String keyword);
    
    // 날씨별 일기 조회
    List<Diary> findByWeatherAndIsDeletedFalseOrderByCreatedAtDesc(Weather weather);
    
    // 기분별 일기 조회
    List<Diary> findByMoodAndIsDeletedFalseOrderByCreatedAtDesc(Mood mood);
    
    // 날짜별 일기 조회
    @Query("SELECT d FROM Diary d WHERE DATE(d.createdAt) = DATE(:date) AND d.isDeleted = false ORDER BY d.createdAt DESC")
    List<Diary> findByCreatedDate(@Param("date") LocalDateTime date);
    
    // 월별 일기 조회
    @Query("SELECT d FROM Diary d WHERE YEAR(d.createdAt) = :year AND MONTH(d.createdAt) = :month AND d.isDeleted = false ORDER BY d.createdAt DESC")
    List<Diary> findByYearAndMonth(@Param("year") int year, @Param("month") int month);
    
    // 최근 일기 조회 (최근 7일)
    @Query("SELECT d FROM Diary d WHERE d.createdAt >= :startDate AND d.isDeleted = false ORDER BY d.createdAt DESC")
    List<Diary> findRecentDiaries(@Param("startDate") LocalDateTime startDate);
    
    // 공개/비공개 일기 조회
    List<Diary> findByIsPrivateAndIsDeletedFalseOrderByCreatedAtDesc(boolean isPrivate);
    
    // 작성자별 공개/비공개 일기 조회
    List<Diary> findByAuthorIdAndIsPrivateAndIsDeletedFalseOrderByCreatedAtDesc(Long authorId, boolean isPrivate);
    
    // 페이징을 위한 메서드들
    Page<Diary> findByIsDeletedFalse(Pageable pageable);
    
    Page<Diary> findByAuthorIdAndIsDeletedFalse(Long authorId, Pageable pageable);
    
    Page<Diary> findByWeatherAndIsDeletedFalse(Weather weather, Pageable pageable);
    
    Page<Diary> findByMoodAndIsDeletedFalse(Mood mood, Pageable pageable);
    
    @Query("SELECT d FROM Diary d WHERE (d.title LIKE %:keyword% OR d.content LIKE %:keyword%) AND d.isDeleted = false")
    Page<Diary> findByTitleContainingOrContentContainingAndIsDeletedFalse(@Param("keyword") String keyword, Pageable pageable);
    
    // 통계를 위한 메서드들
    @Query("SELECT COUNT(d) FROM Diary d WHERE d.author.id = :authorId AND d.isDeleted = false")
    Long countByAuthorId(@Param("authorId") Long authorId);
    
    @Query("SELECT COUNT(d) FROM Diary d WHERE d.author.id = :authorId AND d.weather = :weather AND d.isDeleted = false")
    Long countByAuthorIdAndWeather(@Param("authorId") Long authorId, @Param("weather") Weather weather);
    
    @Query("SELECT COUNT(d) FROM Diary d WHERE d.author.id = :authorId AND d.mood = :mood AND d.isDeleted = false")
    Long countByAuthorIdAndMood(@Param("authorId") Long authorId, @Param("mood") Mood mood);
} 