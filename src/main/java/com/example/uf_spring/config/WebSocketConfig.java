package com.example.uf_spring.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    
    // 연결된 WebSocket 세션들을 저장
    private static final Map<String, WebSocketSession> sessions = new ConcurrentHashMap<>();
    
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new NotificationWebSocketHandler(), "/ws/notify")
               .setAllowedOriginPatterns("*");
        
        registry.addHandler(new ChatWebSocketHandler(), "/ws/chat")
               .setAllowedOriginPatterns("*");
        
        registry.addHandler(new GeneralWebSocketHandler(), "/ws")
               .setAllowedOriginPatterns("*");
    }
    
    // 알림용 WebSocket 핸들러
    public static class NotificationWebSocketHandler extends TextWebSocketHandler {
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String payload = message.getPayload();
            // 모든 연결된 세션에 브로드캐스트
            broadcastToAllSessions("🔔 알림: " + payload);
        }
        
        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            sessions.put(session.getId(), session);
            session.sendMessage(new TextMessage("🔔 알림 서비스에 연결되었습니다!"));
            System.out.println("🔔 새로운 알림 클라이언트 연결: " + session.getId());
        }
        
        @Override
        public void afterConnectionClosed(WebSocketSession session, org.springframework.web.socket.CloseStatus status) throws Exception {
            sessions.remove(session.getId());
            System.out.println("🔔 알림 클라이언트 연결 해제: " + session.getId());
        }
    }
    
    // 채팅용 WebSocket 핸들러
    public static class ChatWebSocketHandler extends TextWebSocketHandler {
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String payload = message.getPayload();
            session.sendMessage(new TextMessage("💬 채팅: " + payload));
        }
        
        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            session.sendMessage(new TextMessage("💬 채팅 서비스에 연결되었습니다!"));
        }
    }
    
    // 일반용 WebSocket 핸들러
    public static class GeneralWebSocketHandler extends TextWebSocketHandler {
        @Override
        protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
            String payload = message.getPayload();
            session.sendMessage(new TextMessage("📡 메시지: " + payload));
        }
        
        @Override
        public void afterConnectionEstablished(WebSocketSession session) throws Exception {
            session.sendMessage(new TextMessage("📡 WebSocket 서비스에 연결되었습니다!"));
        }
    }
    
    // 모든 연결된 세션에 메시지 브로드캐스트
    public static void broadcastToAllSessions(String message) {
        TextMessage textMessage = new TextMessage(message);
        sessions.values().forEach(session -> {
            try {
                if (session.isOpen()) {
                    session.sendMessage(textMessage);
                }
            } catch (Exception e) {
                System.err.println("메시지 전송 실패: " + e.getMessage());
            }
        });
    }
    
    // 특정 세션에 메시지 전송
    public static void sendToSession(String sessionId, String message) {
        WebSocketSession session = sessions.get(sessionId);
        if (session != null && session.isOpen()) {
            try {
                session.sendMessage(new TextMessage(message));
            } catch (Exception e) {
                System.err.println("세션 메시지 전송 실패: " + e.getMessage());
            }
        }
    }
    
    // 연결된 세션 수 반환
    public static int getConnectedSessionCount() {
        return sessions.size();
    }
} 