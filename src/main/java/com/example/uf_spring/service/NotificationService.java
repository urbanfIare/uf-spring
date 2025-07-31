package com.example.uf_spring.service;

import com.example.uf_spring.dto.NotificationDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.example.uf_spring.config.WebSocketConfig;
import java.time.LocalDateTime;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {
    
    // 메모리 기반 알림 저장소 (실제로는 Redis나 DB 사용 권장)
    private final Map<String, String> notificationStore = new ConcurrentHashMap<>();
    private final Map<Long, List<NotificationDto.Response>> userNotifications = new ConcurrentHashMap<>();
    
    // 사용자별 알림 조회
    public List<NotificationDto.Response> getNotificationsByUserId(Long userId) {
        return userNotifications.getOrDefault(userId, new ArrayList<>());
    }
    
    // 읽지 않은 알림 조회
    public List<NotificationDto.Response> getUnreadNotifications(Long userId) {
        return userNotifications.getOrDefault(userId, new ArrayList<>())
                .stream()
                .filter(notification -> !notification.isRead())
                .collect(Collectors.toList());
    }
    
    // 알림 읽음 처리
    public NotificationDto.Response markAsRead(Long notificationId) {
        // 간단한 구현 - 실제로는 DB에서 조회하여 수정
        for (List<NotificationDto.Response> notifications : userNotifications.values()) {
            for (NotificationDto.Response notification : notifications) {
                if (notification.getId().equals(notificationId)) {
                    notification.setRead(true);
                    return notification;
                }
            }
        }
        throw new RuntimeException("알림을 찾을 수 없습니다.");
    }
    
    // 모든 알림 읽음 처리
    public void markAllAsRead(Long userId) {
        List<NotificationDto.Response> notifications = userNotifications.get(userId);
        if (notifications != null) {
            notifications.forEach(notification -> notification.setRead(true));
        }
    }
    
    // 알림 삭제
    public boolean deleteNotification(Long notificationId) {
        for (List<NotificationDto.Response> notifications : userNotifications.values()) {
            for (int i = 0; i < notifications.size(); i++) {
                if (notifications.get(i).getId().equals(notificationId)) {
                    notifications.remove(i);
                    return true;
                }
            }
        }
        return false;
    }
    
    // 알림 생성
    public NotificationDto.Response createNotification(NotificationDto.CreateRequest request) {
        NotificationDto.Response notification = new NotificationDto.Response(
                System.currentTimeMillis(), // 임시 ID
                request.getUserId(),
                request.getMessage(),
                request.getType(),
                false,
                LocalDateTime.now()
        );
        
        userNotifications.computeIfAbsent(request.getUserId(), k -> new ArrayList<>())
                .add(notification);
        
        return notification;
    }
    
    // 실시간 WebSocket 알림 전송
    public void sendNotification(String message) {
        String timestamp = LocalDateTime.now().toString();
        String notification = String.format("[%s] 🔔 알림: %s", timestamp, message);
        
        System.out.println(notification);
        
        // 메모리에 알림 저장
        notificationStore.put(timestamp, notification);
        
        // WebSocket을 통해 실시간 알림 전송
        WebSocketConfig.broadcastToAllSessions(notification);
    }
    
    public void sendNotificationToUser(String userId, String message) {
        String timestamp = LocalDateTime.now().toString();
        String notification = String.format("[%s] 🔔 사용자 %s에게 알림: %s", timestamp, userId, message);
        
        System.out.println(notification);
        
        // 메모리에 사용자별 알림 저장
        notificationStore.put(userId + "_" + timestamp, notification);
        
        // WebSocket을 통해 특정 사용자에게 알림 전송
        // 현재는 모든 세션에 브로드캐스트 (나중에 사용자별 세션 관리 구현)
        WebSocketConfig.broadcastToAllSessions(notification);
    }
    
    public void sendNotificationToAll(String message) {
        String timestamp = LocalDateTime.now().toString();
        String notification = String.format("[%s] 🔔 전체 알림: %s", timestamp, message);
        
        System.out.println(notification);
        
        // 메모리에 전체 알림 저장
        notificationStore.put("all_" + timestamp, notification);
        
        // WebSocket을 통해 전체 알림 전송
        WebSocketConfig.broadcastToAllSessions(notification);
    }
    
    // 저장된 알림 조회
    public Map<String, String> getRecentNotifications() {
        return new ConcurrentHashMap<>(notificationStore);
    }
    
    // 알림 개수 조회
    public int getNotificationCount() {
        return notificationStore.size();
    }
    
    // 알림 초기화
    public void clearNotifications() {
        notificationStore.clear();
        System.out.println("🧹 모든 알림이 초기화되었습니다.");
    }
    
    // 연결된 WebSocket 세션 수 조회
    public int getConnectedSessionCount() {
        return WebSocketConfig.getConnectedSessionCount();
    }
}