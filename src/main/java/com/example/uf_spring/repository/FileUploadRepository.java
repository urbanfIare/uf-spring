package com.example.uf_spring.repository;

import com.example.uf_spring.model.FileUpload;
import com.example.uf_spring.model.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileUploadRepository extends JpaRepository<FileUpload, Long> {
    
    // 게시글에 첨부된 파일들 조회
    List<FileUpload> findByPostAndIsDeletedFalse(Post post);
    
    // 사용자가 업로드한 파일들 조회
    @Query("SELECT f FROM FileUpload f WHERE f.user.id = :userId AND f.isDeleted = false")
    List<FileUpload> findByUserId(@Param("userId") Long userId);
    
    // 게시글 ID로 파일들 조회
    @Query("SELECT f FROM FileUpload f WHERE f.post.id = :postId AND f.isDeleted = false")
    List<FileUpload> findByPostId(@Param("postId") Long postId);
    
    // 파일 타입별 조회
    @Query("SELECT f FROM FileUpload f WHERE f.fileType LIKE %:fileType% AND f.isDeleted = false")
    List<FileUpload> findByFileType(@Param("fileType") String fileType);
} 