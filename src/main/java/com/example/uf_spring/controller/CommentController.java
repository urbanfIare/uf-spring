package com.example.uf_spring.controller;

import com.example.uf_spring.dto.CommentDto;
import com.example.uf_spring.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
@Tag(name = "댓글 관리", description = "댓글 CRUD 및 조회 API")
public class CommentController {

    private final CommentService commentService;

    // 댓글 생성
    @PostMapping("/posts/{postId}")
    @Operation(summary = "댓글 생성", description = "특정 게시글에 새로운 댓글을 작성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "댓글 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<CommentDto.Response> createComment(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId,
            @Parameter(description = "댓글 생성 정보", required = true) @RequestBody CommentDto.CreateRequest request,
            @Parameter(description = "작성자 ID", required = true) @RequestParam Long authorId) {
        try {
            CommentDto.Response response = commentService.createComment(postId, request, authorId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 게시글의 모든 댓글 조회
    @GetMapping("/posts/{postId}")
    @Operation(summary = "게시글 댓글 조회", description = "특정 게시글의 모든 댓글을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "댓글 목록 조회 성공")
    })
    public ResponseEntity<List<CommentDto.Response>> getCommentsByPostId(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long postId) {
        List<CommentDto.Response> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(comments);
    }

    // 댓글 수정
    @PutMapping("/{commentId}")
    @Operation(summary = "댓글 수정", description = "기존 댓글을 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "댓글 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "수정할 댓글을 찾을 수 없음")
    })
    public ResponseEntity<CommentDto.Response> updateComment(
            @Parameter(description = "댓글 ID", required = true) @PathVariable Long commentId,
            @Parameter(description = "수정할 댓글 정보", required = true) @RequestBody CommentDto.UpdateRequest request,
            @Parameter(description = "작성자 ID", required = true) @RequestParam Long authorId) {
        try {
            CommentDto.Response response = commentService.updateComment(commentId, request, authorId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 댓글 삭제
    @DeleteMapping("/{commentId}")
    @Operation(summary = "댓글 삭제", description = "특정 댓글을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "댓글 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "삭제할 댓글을 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteComment(
            @Parameter(description = "댓글 ID", required = true) @PathVariable Long commentId,
            @Parameter(description = "작성자 ID", required = true) @RequestParam Long authorId) {
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
    @Operation(summary = "사용자 댓글 조회", description = "특정 사용자가 작성한 모든 댓글을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "사용자 댓글 목록 조회 성공")
    })
    public ResponseEntity<List<CommentDto.Response>> getCommentsByAuthorId(
            @Parameter(description = "작성자 ID", required = true) @PathVariable Long authorId) {
        List<CommentDto.Response> comments = commentService.getCommentsByAuthorId(authorId);
        return ResponseEntity.ok(comments);
    }
} 