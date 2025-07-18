package com.example.uf_spring.controller;

import com.example.uf_spring.dto.CommentDto;
import com.example.uf_spring.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
public class CommentController {

    @Autowired
    private CommentService commentService;

    // 댓글 생성
    @PostMapping("/posts/{postId}")
    public ResponseEntity<CommentDto.Response> createComment(
            @PathVariable Long postId,
            @RequestBody CommentDto.CreateRequest request,
            @RequestParam Long authorId) {
        try {
            CommentDto.Response response = commentService.createComment(postId, request, authorId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 게시글의 모든 댓글 조회
    @GetMapping("/posts/{postId}")
    public ResponseEntity<List<CommentDto.Response>> getCommentsByPostId(@PathVariable Long postId) {
        List<CommentDto.Response> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto.Response> updateComment(
            @PathVariable Long commentId,
            @RequestBody CommentDto.UpdateRequest request,
            @RequestParam Long authorId) {
        try {
            CommentDto.Response response = commentService.updateComment(commentId, request, authorId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long commentId,
            @RequestParam Long authorId) {
        try {
            boolean deleted = commentService.deleteComment(commentId, authorId);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 사용자의 모든 댓글 조회
    @GetMapping("/author/{authorId}")
    public ResponseEntity<List<CommentDto.Response>> getCommentsByAuthorId(@PathVariable Long authorId) {
        List<CommentDto.Response> comments = commentService.getCommentsByAuthorId(authorId);
        return ResponseEntity.ok(comments);
    }
} 