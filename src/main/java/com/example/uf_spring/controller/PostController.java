package com.example.uf_spring.controller;

import com.example.uf_spring.dto.PostDto;
import com.example.uf_spring.model.PostCategory;
import com.example.uf_spring.service.PostService;
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
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Tag(name = "게시글 관리", description = "게시글 CRUD 및 검색 API")
public class PostController {

    private final PostService postService;

    // 게시글 생성
    @PostMapping
    @Operation(summary = "게시글 생성", description = "새로운 게시글을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시글 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<PostDto.Response> createPost(
            @Parameter(description = "게시글 생성 정보", required = true) @RequestBody PostDto.CreateRequest request,
            @Parameter(description = "작성자 ID", required = true) @RequestParam Long authorId) {
        try {
            PostDto.Response response = postService.createPost(request, authorId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 모든 게시글 조회
    @GetMapping
    @Operation(summary = "모든 게시글 조회", description = "시스템의 모든 게시글을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시글 목록 조회 성공")
    })
    public ResponseEntity<List<PostDto.Response>> getAllPosts() {
        List<PostDto.Response> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 게시글 상세 조회
    @GetMapping("/{id}")
    @Operation(summary = "게시글 상세 조회", description = "특정 게시글의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시글 조회 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<PostDto.Response> getPostById(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long id) {
        try {
            PostDto.Response response = postService.getPostById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 게시글 수정
    @PutMapping("/{id}")
    @Operation(summary = "게시글 수정", description = "기존 게시글을 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시글 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "수정할 게시글을 찾을 수 없음")
    })
    public ResponseEntity<PostDto.Response> updatePost(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long id,
            @Parameter(description = "수정할 게시글 정보", required = true) @RequestBody PostDto.UpdateRequest request,
            @Parameter(description = "작성자 ID", required = true) @RequestParam Long authorId) {
        try {
            PostDto.Response response = postService.updatePost(id, request, authorId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "게시글 삭제", description = "특정 게시글을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "게시글 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "삭제할 게시글을 찾을 수 없음")
    })
    public ResponseEntity<Void> deletePost(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long id,
            @Parameter(description = "작성자 ID", required = true) @RequestParam Long authorId) {
        try {
            boolean deleted = postService.deletePost(id, authorId);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 카테고리별 게시글 조회
    @GetMapping("/category/{category}")
    @Operation(summary = "카테고리별 게시글 조회", description = "특정 카테고리의 게시글들을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "카테고리별 게시글 조회 성공")
    })
    public ResponseEntity<List<PostDto.Response>> getPostsByCategory(
            @Parameter(description = "게시글 카테고리", required = true) @PathVariable PostCategory category) {
        List<PostDto.Response> posts = postService.getPostsByCategory(category);
        return ResponseEntity.ok(posts);
    }

    // 검색 기능
    @GetMapping("/search")
    @Operation(summary = "게시글 검색", description = "키워드로 게시글을 검색합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "검색 결과 반환")
    })
    public ResponseEntity<List<PostDto.Response>> searchPosts(
            @Parameter(description = "검색 키워드", required = true) @RequestParam String keyword) {
        List<PostDto.Response> posts = postService.searchPosts(keyword);
        return ResponseEntity.ok(posts);
    }

    // 인기 게시글 조회
    @GetMapping("/popular")
    @Operation(summary = "인기 게시글 조회", description = "인기 있는 게시글들을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "인기 게시글 조회 성공")
    })
    public ResponseEntity<List<PostDto.Response>> getPopularPosts() {
        List<PostDto.Response> posts = postService.getPopularPosts();
        return ResponseEntity.ok(posts);
    }

    // 가장 좋아요가 많은 게시글 조회
    @GetMapping("/most-liked")
    @Operation(summary = "가장 좋아요가 많은 게시글 조회", description = "좋아요가 가장 많은 게시글들을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "좋아요 많은 게시글 조회 성공")
    })
    public ResponseEntity<List<PostDto.Response>> getMostLikedPosts() {
        List<PostDto.Response> posts = postService.getMostLikedPosts();
        return ResponseEntity.ok(posts);
    }

    // 최근 게시글 조회
    @GetMapping("/recent")
    @Operation(summary = "최근 게시글 조회", description = "최근에 작성된 게시글들을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "최근 게시글 조회 성공")
    })
    public ResponseEntity<List<PostDto.Response>> getRecentPosts() {
        List<PostDto.Response> posts = postService.getRecentPosts();
        return ResponseEntity.ok(posts);
    }

    // 게시글 좋아요
    @PostMapping("/{id}/like")
    @Operation(summary = "게시글 좋아요", description = "특정 게시글에 좋아요를 추가합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "좋아요 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<PostDto.Response> likePost(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long id) {
        try {
            PostDto.Response response = postService.likePost(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 게시글 좋아요 취소
    @PostMapping("/{id}/unlike")
    @Operation(summary = "게시글 좋아요 취소", description = "특정 게시글의 좋아요를 취소합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "좋아요 취소 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<PostDto.Response> unlikePost(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long id) {
        try {
            PostDto.Response response = postService.unlikePost(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 페이지네이션 검색
    @PostMapping("/search")
    @Operation(summary = "페이지네이션 검색", description = "페이지네이션을 지원하는 게시글 검색을 수행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "검색 결과 반환")
    })
    public ResponseEntity<PostDto.PageResponse> searchPostsWithPaging(
            @Parameter(description = "검색 요청 정보", required = true) @RequestBody PostDto.SearchRequest request) {
        PostDto.PageResponse response = postService.searchPostsWithPaging(request);
        return ResponseEntity.ok(response);
    }

    // 조회수 증가
    @PostMapping("/{id}/view")
    @Operation(summary = "조회수 증가", description = "특정 게시글의 조회수를 증가시킵니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "조회수 증가 성공"),
        @ApiResponse(responseCode = "404", description = "게시글을 찾을 수 없음")
    })
    public ResponseEntity<Void> incrementViewCount(
            @Parameter(description = "게시글 ID", required = true) @PathVariable Long id) {
        try {
            postService.incrementViewCount(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 