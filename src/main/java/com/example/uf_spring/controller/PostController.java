package com.example.uf_spring.controller;

import com.example.uf_spring.dto.PostDto;
import com.example.uf_spring.model.PostCategory;
import com.example.uf_spring.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    @Autowired
    private PostService postService;

    // 게시글 생성
    @PostMapping
    public ResponseEntity<PostDto.Response> createPost(
            @RequestBody PostDto.CreateRequest request,
            @RequestParam Long authorId) {
        try {
            PostDto.Response response = postService.createPost(request, authorId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 모든 게시글 조회
    @GetMapping
    public ResponseEntity<List<PostDto.Response>> getAllPosts() {
        List<PostDto.Response> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    // 게시글 상세 조회
    @GetMapping("/{id}")
    public ResponseEntity<PostDto.Response> getPostById(@PathVariable Long id) {
        try {
            PostDto.Response response = postService.getPostById(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 게시글 수정
    @PutMapping("/{id}")
    public ResponseEntity<PostDto.Response> updatePost(
            @PathVariable Long id,
            @RequestBody PostDto.UpdateRequest request,
            @RequestParam Long authorId) {
        try {
            PostDto.Response response = postService.updatePost(id, request, authorId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 게시글 삭제
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(
            @PathVariable Long id,
            @RequestParam Long authorId) {
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
    public ResponseEntity<List<PostDto.Response>> getPostsByCategory(
            @PathVariable PostCategory category) {
        List<PostDto.Response> posts = postService.getPostsByCategory(category);
        return ResponseEntity.ok(posts);
    }

    // 검색 기능
    @GetMapping("/search")
    public ResponseEntity<List<PostDto.Response>> searchPosts(
            @RequestParam String keyword) {
        List<PostDto.Response> posts = postService.searchPosts(keyword);
        return ResponseEntity.ok(posts);
    }

    // 인기 게시글 조회
    @GetMapping("/popular")
    public ResponseEntity<List<PostDto.Response>> getPopularPosts() {
        List<PostDto.Response> posts = postService.getPopularPosts();
        return ResponseEntity.ok(posts);
    }

    // 좋아요 많은 게시글 조회
    @GetMapping("/most-liked")
    public ResponseEntity<List<PostDto.Response>> getMostLikedPosts() {
        List<PostDto.Response> posts = postService.getMostLikedPosts();
        return ResponseEntity.ok(posts);
    }

    // 최근 게시글 조회
    @GetMapping("/recent")
    public ResponseEntity<List<PostDto.Response>> getRecentPosts() {
        List<PostDto.Response> posts = postService.getRecentPosts();
        return ResponseEntity.ok(posts);
    }

    // 좋아요 증가
    @PostMapping("/{id}/like")
    public ResponseEntity<PostDto.Response> likePost(@PathVariable Long id) {
        try {
            PostDto.Response response = postService.likePost(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 좋아요 감소
    @PostMapping("/{id}/unlike")
    public ResponseEntity<PostDto.Response> unlikePost(@PathVariable Long id) {
        try {
            PostDto.Response response = postService.unlikePost(id);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    // 고급 검색 및 페이징
    @PostMapping("/search")
    public ResponseEntity<PostDto.PageResponse> searchPostsWithPaging(@RequestBody PostDto.SearchRequest request) {
        try {
            PostDto.PageResponse response = postService.searchPostsWithPaging(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
    
    // 조회수 증가 (별도 엔드포인트)
    @PostMapping("/{id}/view")
    public ResponseEntity<Void> incrementViewCount(@PathVariable Long id) {
        try {
            postService.incrementViewCount(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 