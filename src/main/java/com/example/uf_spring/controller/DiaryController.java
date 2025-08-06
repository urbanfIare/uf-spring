package com.example.uf_spring.controller;

import com.example.uf_spring.dto.DiaryDto;
import com.example.uf_spring.model.Diary.Weather;
import com.example.uf_spring.model.Diary.Mood;
import com.example.uf_spring.service.DiaryService;
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
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/diaries")
@RequiredArgsConstructor
@Tag(name = "일기 관리", description = "일기 CRUD 및 검색 API")
public class DiaryController {

    private final DiaryService diaryService;

    // 일기 생성
    @PostMapping
    @Operation(summary = "일기 생성", description = "새로운 일기를 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "일기 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<DiaryDto.Response> createDiary(
            @Parameter(description = "일기 생성 정보", required = true) @RequestBody DiaryDto.CreateRequest request,
            @Parameter(description = "작성자 ID", required = true) @RequestParam Long authorId) {
        try {
            DiaryDto.Response response = diaryService.createDiary(request, authorId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 작성자별 일기 조회
    @GetMapping("/author/{authorId}")
    @Operation(summary = "작성자별 일기 조회", description = "특정 작성자의 모든 일기를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "일기 목록 조회 성공")
    })
    public ResponseEntity<List<DiaryDto.Response>> getDiariesByAuthor(
            @Parameter(description = "작성자 ID", required = true) @PathVariable Long authorId,
            HttpServletRequest request) {
        
        // 디버깅을 위한 로그 추가
        String authHeader = request.getHeader("Authorization");
        System.out.println("=== Diary API 호출 ===");
        System.out.println("요청 URL: " + request.getRequestURI());
        System.out.println("Authorization 헤더: " + authHeader);
        System.out.println("작성자 ID: " + authorId);
        
        List<DiaryDto.Response> diaries = diaryService.getAllDiariesByAuthor(authorId);
        return ResponseEntity.ok(diaries);
    }

    // 일기 상세 조회
    @GetMapping("/{id}")
    @Operation(summary = "일기 상세 조회", description = "특정 일기의 상세 정보를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "일기 조회 성공"),
        @ApiResponse(responseCode = "404", description = "일기를 찾을 수 없음")
    })
    public ResponseEntity<DiaryDto.Response> getDiaryById(
            @Parameter(description = "일기 ID", required = true) @PathVariable Long id,
            @Parameter(description = "조회자 ID", required = true) @RequestParam Long authorId) {
        try {
            DiaryDto.Response response = diaryService.getDiaryById(id, authorId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 일기 수정
    @PutMapping("/{id}")
    @Operation(summary = "일기 수정", description = "기존 일기를 수정합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "일기 수정 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터"),
        @ApiResponse(responseCode = "404", description = "수정할 일기를 찾을 수 없음")
    })
    public ResponseEntity<DiaryDto.Response> updateDiary(
            @Parameter(description = "일기 ID", required = true) @PathVariable Long id,
            @Parameter(description = "수정할 일기 정보", required = true) @RequestBody DiaryDto.UpdateRequest request,
            @Parameter(description = "작성자 ID", required = true) @RequestParam Long authorId) {
        try {
            DiaryDto.Response response = diaryService.updateDiary(id, request, authorId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 일기 삭제
    @DeleteMapping("/{id}")
    @Operation(summary = "일기 삭제", description = "특정 일기를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "일기 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청"),
        @ApiResponse(responseCode = "404", description = "삭제할 일기를 찾을 수 없음")
    })
    public ResponseEntity<Boolean> deleteDiary(
            @Parameter(description = "일기 ID", required = true) @PathVariable Long id,
            @Parameter(description = "작성자 ID", required = true) @RequestParam Long authorId) {
        try {
            boolean result = diaryService.deleteDiary(id, authorId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 날씨별 일기 조회
    @GetMapping("/weather/{weather}")
    @Operation(summary = "날씨별 일기 조회", description = "특정 날씨의 일기들을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "날씨별 일기 조회 성공")
    })
    public ResponseEntity<List<DiaryDto.Response>> getDiariesByWeather(
            @Parameter(description = "날씨", required = true) @PathVariable Weather weather,
            @Parameter(description = "조회자 ID", required = true) @RequestParam Long authorId) {
        List<DiaryDto.Response> diaries = diaryService.getDiariesByWeather(weather, authorId);
        return ResponseEntity.ok(diaries);
    }

    // 기분별 일기 조회
    @GetMapping("/mood/{mood}")
    @Operation(summary = "기분별 일기 조회", description = "특정 기분의 일기들을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "기분별 일기 조회 성공")
    })
    public ResponseEntity<List<DiaryDto.Response>> getDiariesByMood(
            @Parameter(description = "기분", required = true) @PathVariable Mood mood,
            @Parameter(description = "조회자 ID", required = true) @RequestParam Long authorId) {
        List<DiaryDto.Response> diaries = diaryService.getDiariesByMood(mood, authorId);
        return ResponseEntity.ok(diaries);
    }

    // 날짜별 일기 조회
    @GetMapping("/date")
    @Operation(summary = "날짜별 일기 조회", description = "특정 날짜의 일기들을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "날짜별 일기 조회 성공")
    })
    public ResponseEntity<List<DiaryDto.Response>> getDiariesByDate(
            @Parameter(description = "조회할 날짜", required = true) @RequestParam LocalDateTime date,
            @Parameter(description = "조회자 ID", required = true) @RequestParam Long authorId) {
        List<DiaryDto.Response> diaries = diaryService.getDiariesByDate(date, authorId);
        return ResponseEntity.ok(diaries);
    }

    // 월별 일기 조회
    @GetMapping("/month")
    @Operation(summary = "월별 일기 조회", description = "특정 월의 일기들을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "월별 일기 조회 성공")
    })
    public ResponseEntity<List<DiaryDto.Response>> getDiariesByMonth(
            @Parameter(description = "년도", required = true) @RequestParam int year,
            @Parameter(description = "월", required = true) @RequestParam int month,
            @Parameter(description = "조회자 ID", required = true) @RequestParam Long authorId) {
        List<DiaryDto.Response> diaries = diaryService.getDiariesByMonth(year, month, authorId);
        return ResponseEntity.ok(diaries);
    }

    // 최근 일기 조회
    @GetMapping("/recent")
    @Operation(summary = "최근 일기 조회", description = "최근 7일간의 일기들을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "최근 일기 조회 성공")
    })
    public ResponseEntity<List<DiaryDto.Response>> getRecentDiaries(
            @Parameter(description = "조회자 ID", required = true) @RequestParam Long authorId) {
        List<DiaryDto.Response> diaries = diaryService.getRecentDiaries(authorId);
        return ResponseEntity.ok(diaries);
    }

    // 일기 검색
    @GetMapping("/search")
    @Operation(summary = "일기 검색", description = "키워드로 일기를 검색합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "검색 결과 반환")
    })
    public ResponseEntity<List<DiaryDto.Response>> searchDiaries(
            @Parameter(description = "검색 키워드", required = true) @RequestParam String keyword,
            @Parameter(description = "조회자 ID", required = true) @RequestParam Long authorId) {
        List<DiaryDto.Response> diaries = diaryService.searchDiaries(keyword, authorId);
        return ResponseEntity.ok(diaries);
    }

    // 페이징 검색
    @PostMapping("/search")
    @Operation(summary = "페이지네이션 검색", description = "페이지네이션을 지원하는 일기 검색을 수행합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "검색 결과 반환")
    })
    public ResponseEntity<DiaryDto.PageResponse> searchDiariesWithPaging(
            @Parameter(description = "검색 요청 정보", required = true) @RequestBody DiaryDto.SearchRequest request,
            @Parameter(description = "조회자 ID", required = true) @RequestParam Long authorId) {
        DiaryDto.PageResponse response = diaryService.searchDiariesWithPaging(request, authorId);
        return ResponseEntity.ok(response);
    }

    // 일기 통계 조회
    @GetMapping("/statistics")
    @Operation(summary = "일기 통계 조회", description = "일기 작성 통계를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "통계 조회 성공")
    })
    public ResponseEntity<DiaryDto.StatisticsResponse> getDiaryStatistics(
            @Parameter(description = "작성자 ID", required = true) @RequestParam Long authorId) {
        DiaryDto.StatisticsResponse response = diaryService.getDiaryStatistics(authorId);
        return ResponseEntity.ok(response);
    }

    // 일기 이미지 업로드
    @PostMapping("/{diaryId}/images")
    @Operation(summary = "일기 이미지 업로드", description = "일기에 이미지를 업로드합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이미지 업로드 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<String> uploadImage(
            @Parameter(description = "일기 ID", required = true) @PathVariable Long diaryId,
            @Parameter(description = "원본 파일명", required = true) @RequestParam String originalFileName,
            @Parameter(description = "저장된 파일명", required = true) @RequestParam String storedFileName,
            @Parameter(description = "파일 경로", required = true) @RequestParam String filePath,
            @Parameter(description = "파일 크기", required = true) @RequestParam Long fileSize,
            @Parameter(description = "파일 타입", required = true) @RequestParam String contentType) {
        try {
            diaryService.uploadImage(originalFileName, storedFileName, filePath, fileSize, contentType, diaryId);
            return ResponseEntity.ok("이미지 업로드 성공");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 일기 이미지 조회
    @GetMapping("/{diaryId}/images")
    @Operation(summary = "일기 이미지 조회", description = "일기에 첨부된 이미지들을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이미지 조회 성공")
    })
    public ResponseEntity<List<String>> getDiaryImages(
            @Parameter(description = "일기 ID", required = true) @PathVariable Long diaryId) {
        List<String> imageUrls = diaryService.getDiaryImageUrls(diaryId);
        return ResponseEntity.ok(imageUrls);
    }

    // 일기 이미지 삭제
    @DeleteMapping("/images/{imageId}")
    @Operation(summary = "일기 이미지 삭제", description = "일기에 첨부된 이미지를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "이미지 삭제 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청")
    })
    public ResponseEntity<Boolean> deleteImage(
            @Parameter(description = "이미지 ID", required = true) @PathVariable Long imageId,
            @Parameter(description = "작성자 ID", required = true) @RequestParam Long authorId) {
        try {
            boolean result = diaryService.deleteImage(imageId, authorId);
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 