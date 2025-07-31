package com.example.uf_spring.controller;

import com.example.uf_spring.dto.BookmarkDto;
import com.example.uf_spring.service.BookmarkService;
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
@RequestMapping("/api/bookmarks")
@RequiredArgsConstructor
@Tag(name = "북마크 관리", description = "북마크 추가, 삭제, 조회 API")
public class BookmarkController {

    private final BookmarkService bookmarkService;

    // 북마크 추가
    @PostMapping
    @Operation(summary = "북마크 추가", description = "게시글을 북마크에 추가합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "북마크 추가 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<BookmarkDto.Response> addBookmark(
            @Parameter(description = "북마크 정보", required = true) @RequestBody BookmarkDto.CreateRequest request) {
        try {
            BookmarkDto.Response response = bookmarkService.addBookmark(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 사용자의 북마크 조회
    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자 북마크 조회", description = "특정 사용자의 모든 북마크를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "북마크 목록 조회 성공")
    })
    public ResponseEntity<List<BookmarkDto.Response>> getBookmarksByUserId(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId) {
        List<BookmarkDto.Response> bookmarks = bookmarkService.getBookmarksByUserId(userId);
        return ResponseEntity.ok(bookmarks);
    }

    // 북마크 삭제
    @DeleteMapping("/{bookmarkId}")
    @Operation(summary = "북마크 삭제", description = "특정 북마크를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "북마크 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "삭제할 북마크를 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteBookmark(
            @Parameter(description = "북마크 ID", required = true) @PathVariable Long bookmarkId) {
        try {
            boolean deleted = bookmarkService.deleteBookmark(bookmarkId);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 북마크 확인
    @GetMapping("/check")
    @Operation(summary = "북마크 확인", description = "특정 게시글이 북마크되어 있는지 확인합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "북마크 확인 완료")
    })
    public ResponseEntity<Boolean> checkBookmark(
            @Parameter(description = "사용자 ID", required = true) @RequestParam Long userId,
            @Parameter(description = "게시글 ID", required = true) @RequestParam Long postId) {
        boolean isBookmarked = bookmarkService.isBookmarked(userId, postId);
        return ResponseEntity.ok(isBookmarked);
    }
} 