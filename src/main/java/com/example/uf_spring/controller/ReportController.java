package com.example.uf_spring.controller;

import com.example.uf_spring.dto.ReportDto;
import com.example.uf_spring.model.Report.ReportStatus;
import com.example.uf_spring.service.ReportService;
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
@RequestMapping("/api/reports")
@RequiredArgsConstructor
@Tag(name = "신고 관리", description = "게시글/댓글 신고 및 신고 처리 API")
public class ReportController {

    private final ReportService reportService;

    // 신고 생성
    @PostMapping
    @Operation(summary = "신고 생성", description = "게시글이나 댓글을 신고합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "신고 생성 성공"),
        @ApiResponse(responseCode = "400", description = "잘못된 요청 데이터")
    })
    public ResponseEntity<ReportDto.Response> createReport(
            @Parameter(description = "신고 정보", required = true) @RequestBody ReportDto.CreateRequest request) {
        try {
            ReportDto.Response response = reportService.createReport(request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 모든 신고 조회
    @GetMapping
    @Operation(summary = "모든 신고 조회", description = "시스템의 모든 신고를 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "신고 목록 조회 성공")
    })
    public ResponseEntity<List<ReportDto.Response>> getAllReports() {
        List<ReportDto.Response> reports = reportService.getReports(null);
        return ResponseEntity.ok(reports);
    }

    // 신고 처리
    @PutMapping("/{reportId}/process")
    @Operation(summary = "신고 처리", description = "특정 신고를 처리합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "신고 처리 성공"),
        @ApiResponse(responseCode = "404", description = "처리할 신고를 찾을 수 없음")
    })
    public ResponseEntity<ReportDto.Response> processReport(
            @Parameter(description = "신고 ID", required = true) @PathVariable Long reportId,
            @Parameter(description = "처리 상태(PENDING/REJECTED/RESOLVED)", required = true) @RequestParam ReportStatus status) {
        try {
            ReportDto.Response response = reportService.updateStatus(reportId, status);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 신고 삭제
    @DeleteMapping("/{reportId}")
    @Operation(summary = "신고 삭제", description = "특정 신고를 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "신고 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "삭제할 신고를 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteReport(
            @Parameter(description = "신고 ID", required = true) @PathVariable Long reportId) {
        try {
            reportService.deleteReport(reportId);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
} 