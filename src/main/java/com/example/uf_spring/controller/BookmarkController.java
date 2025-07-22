package com.example.uf_spring.controller;

import com.example.uf_spring.dto.BookmarkDto;
import com.example.uf_spring.service.BookmarkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/bookmarks")
public class BookmarkController {
    @Autowired
    private BookmarkService bookmarkService;

    // 북마크 추가
    @PostMapping
    public ResponseEntity<BookmarkDto.Response> addBookmark(@RequestParam Long userId, @RequestParam Long postId) {
        try {
            BookmarkDto.Response response = bookmarkService.addBookmark(userId, postId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 북마크 삭제
    @DeleteMapping
    public ResponseEntity<Void> removeBookmark(@RequestParam Long userId, @RequestParam Long postId) {
        try {
            bookmarkService.removeBookmark(userId, postId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 내 북마크 목록 조회
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<BookmarkDto.Response>> getBookmarksByUser(@PathVariable Long userId) {
        try {
            List<BookmarkDto.Response> list = bookmarkService.getBookmarksByUser(userId);
            return ResponseEntity.ok(list);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 북마크 여부 확인
    @GetMapping("/exists")
    public ResponseEntity<Boolean> isBookmarked(@RequestParam Long userId, @RequestParam Long postId) {
        try {
            boolean exists = bookmarkService.isBookmarked(userId, postId);
            return ResponseEntity.ok(exists);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 