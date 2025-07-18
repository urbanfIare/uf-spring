package com.example.uf_spring.repository;

import com.example.uf_spring.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    
    // 게시글별 댓글 조회 (삭제되지 않은 것만)
    List<Comment> findByPostIdAndIsDeletedFalseOrderByCreatedAtAsc(Long postId);
    
    // 작성자별 댓글 조회
    List<Comment> findByAuthorIdAndIsDeletedFalseOrderByCreatedAtDesc(Long authorId);
    
    // 게시글의 댓글 수 조회
    long countByPostIdAndIsDeletedFalse(Long postId);
} 