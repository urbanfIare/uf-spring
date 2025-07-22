package com.example.uf_spring.controller;

import com.example.uf_spring.dto.ReportDto;
import com.example.uf_spring.model.Report.ReportStatus;
import com.example.uf_spring.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @Autowired
    private ReportService reportService;

    // 신고 등록
    @PostMapping
    public ResponseEntity<ReportDto.Response> createReport(@RequestBody ReportDto.CreateRequest req) {
        try {
            ReportDto.Response response = reportService.createReport(req);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 전체/상태별 신고 목록 (관리자)
    @GetMapping
    public ResponseEntity<List<ReportDto.Response>> getReports(@RequestParam(required = false) ReportStatus status) {
        try {
            List<ReportDto.Response> list = reportService.getReports(status);
            return ResponseEntity.ok(list);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    // 신고 처리(상태 변경, 관리자)
    @PutMapping("/{reportId}")
    public ResponseEntity<ReportDto.Response> updateStatus(@PathVariable Long reportId, @RequestParam ReportStatus status) {
        try {
            ReportDto.Response response = reportService.updateStatus(reportId, status);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().build();
        }
    }
} 