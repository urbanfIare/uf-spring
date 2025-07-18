package com.example.uf_spring.repository;

import com.example.uf_spring.model.Post;
import com.example.uf_spring.model.PostCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    
    // 삭제되지 않은 게시글만 조회
    List<Post> findByIsDeletedFalseOrderByCreatedAtDesc();
    
    // 카테고리별 게시글 조회
    List<Post> findByCategoryAndIsDeletedFalseOrderByCreatedAtDesc(PostCategory category);
    
    // 작성자별 게시글 조회
    List<Post> findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(Long authorId);
    
    // 제목으로 검색
    List<Post> findByTitleContainingAndIsDeletedFalseOrderByCreatedAtDesc(String title);
    
    // 내용으로 검색
    List<Post> findByContentContainingAndIsDeletedFalseOrderByCreatedAtDesc(String content);
    
    // 제목 또는 내용으로 검색
    @Query("SELECT p FROM Post p WHERE (p.title LIKE %:keyword% OR p.content LIKE %:keyword%) AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Post> findByTitleOrContentContaining(@Param("keyword") String keyword);
    
    // 조회수 높은 순으로 조회
    List<Post> findByIsDeletedFalseOrderByViewCountDesc();
    
    // 좋아요 높은 순으로 조회
    List<Post> findByIsDeletedFalseOrderByLikeCountDesc();
    
    // 최근 게시글 조회 (최근 7일)
    @Query("SELECT p FROM Post p WHERE p.createdAt >= :startDate AND p.isDeleted = false ORDER BY p.createdAt DESC")
    List<Post> findRecentPosts(@Param("startDate") java.time.LocalDateTime startDate);
} 