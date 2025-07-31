package com.example.uf_spring.controller;

import com.example.uf_spring.dto.NotificationDto;
import com.example.uf_spring.service.NotificationService;
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
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "알림 관리", description = "실시간 알림 및 알림 관리 API")
public class NotificationController {

    private final NotificationService notificationService;

    // 사용자의 모든 알림 조회
    @GetMapping("/user/{userId}")
    @Operation(summary = "사용자 알림 조회", description = "특정 사용자의 모든 알림을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공")
    })
    public ResponseEntity<List<NotificationDto.Response>> getNotificationsByUserId(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId) {
        List<NotificationDto.Response> notifications = notificationService.getNotificationsByUserId(userId);
        return ResponseEntity.ok(notifications);
    }

    // 읽지 않은 알림 조회
    @GetMapping("/user/{userId}/unread")
    @Operation(summary = "읽지 않은 알림 조회", description = "특정 사용자의 읽지 않은 알림을 조회합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "읽지 않은 알림 조회 성공")
    })
    public ResponseEntity<List<NotificationDto.Response>> getUnreadNotifications(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId) {
        List<NotificationDto.Response> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(notifications);
    }

    // 알림 읽음 처리
    @PutMapping("/{notificationId}/read")
    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 상태로 변경합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
        @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음")
    })
    public ResponseEntity<NotificationDto.Response> markAsRead(
            @Parameter(description = "알림 ID", required = true) @PathVariable Long notificationId) {
        try {
            NotificationDto.Response response = notificationService.markAsRead(notificationId);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 모든 알림 읽음 처리
    @PutMapping("/user/{userId}/read-all")
    @Operation(summary = "모든 알림 읽음 처리", description = "사용자의 모든 알림을 읽음 상태로 변경합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "모든 알림 읽음 처리 성공")
    })
    public ResponseEntity<Void> markAllAsRead(
            @Parameter(description = "사용자 ID", required = true) @PathVariable Long userId) {
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok().build();
    }

    // 알림 삭제
    @DeleteMapping("/{notificationId}")
    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "알림 삭제 성공"),
        @ApiResponse(responseCode = "404", description = "삭제할 알림을 찾을 수 없음")
    })
    public ResponseEntity<Void> deleteNotification(
            @Parameter(description = "알림 ID", required = true) @PathVariable Long notificationId) {
        try {
            boolean deleted = notificationService.deleteNotification(notificationId);
            if (deleted) {
                return ResponseEntity.ok().build();
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // 알림 생성 (테스트용)
    @PostMapping("/test")
    @Operation(summary = "테스트 알림 생성", description = "테스트용 알림을 생성합니다.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "테스트 알림 생성 성공")
    })
    public ResponseEntity<NotificationDto.Response> createTestNotification(
            @Parameter(description = "테스트 알림 정보", required = true) @RequestBody NotificationDto.CreateRequest request) {
        NotificationDto.Response response = notificationService.createNotification(request);
        return ResponseEntity.ok(response);
    }
} 