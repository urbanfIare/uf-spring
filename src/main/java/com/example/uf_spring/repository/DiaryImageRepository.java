package com.example.uf_spring.repository;

import com.example.uf_spring.model.DiaryImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaryImageRepository extends JpaRepository<DiaryImage, Long> {
    
    // 일기별 이미지 조회
    List<DiaryImage> findByDiaryIdAndIsDeletedFalseOrderByUploadedAtAsc(Long diaryId);
    
    // 삭제되지 않은 이미지만 조회
    List<DiaryImage> findByIsDeletedFalse();
    
    // 파일명으로 이미지 조회
    DiaryImage findByStoredFileNameAndIsDeletedFalse(String storedFileName);
    
    // 일기 삭제 시 관련 이미지들도 삭제 처리
    List<DiaryImage> findByDiaryId(Long diaryId);
} 