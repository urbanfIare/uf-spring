package com.example.uf_spring.controller;

import com.example.uf_spring.dto.TagDto;
import com.example.uf_spring.service.TagService;
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
@RequestMapping("/api/tags")
@RequiredArgsConstructor
@Tag(name = "태그 관리", description = "태그 생성, 조회, 삭제 API")
public class TagController {

    private final TagService tagService;

    // 모든 태그 조회
    @GetMapping
    @Operation(summary = "모든 태그 조회", description = "시스템의 모든 태그를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "태그 목록 조회 성공")
    })
    public ResponseEntity<List<TagDto.Response>> getAllTags() {
        List<TagDto.Response> tags = tagService.getAllTags();
        return ResponseEntity.ok(tags);
    }

    // 태그 생성
    @PostMapping
    @Operation(summary = "태그 생성", description = "새로운 태그를 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "태그 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<TagDto.Response> createTag(
            @Parameter(description = "태그 생성 정보", required = true) @RequestBody TagDto.CreateRequest request) {
        try {
            TagDto.Response response = tagService.createTag(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 태그 삭제
    @DeleteMapping("/{tagId}")
    @Operation(summary = "태그 삭제", description = "특정 태그를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "태그 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "삭제할 태그를 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteTag(
            @Parameter(description = "태그 ID", required = true) @PathVariable Long tagId) {
        try {
            boolean deleted = tagService.deleteTag(tagId);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 