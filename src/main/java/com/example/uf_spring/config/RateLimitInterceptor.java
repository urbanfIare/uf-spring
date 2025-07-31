package com.example.uf_spring.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class RateLimitInterceptor implements HandlerInterceptor {

    private final Map<String, RequestCounter> requestCounters = new ConcurrentHashMap<>();
    private static final int MAX_REQUESTS_PER_MINUTE = 60;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String clientIp = getClientIp(request);
        String key = clientIp + ":" + request.getRequestURI();
        
        RequestCounter counter = requestCounters.computeIfAbsent(key, k -> new RequestCounter());
        
        if (counter.isRateLimited()) {
            log.warn("Rate limit exceeded for IP: {} on path: {}", clientIp, request.getRequestURI());
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            response.getWriter().write("Rate limit exceeded. Please try again later.");
            return false;
        }
        
        counter.increment();
        return true;
    }

    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    private static class RequestCounter {
        private int count = 0;
        private long resetTime = System.currentTimeMillis() + 60000; // 1분

        public synchronized boolean isRateLimited() {
            long now = System.currentTimeMillis();
            if (now > resetTime) {
                count = 0;
                resetTime = now + 60000;
            }
            return count >= MAX_REQUESTS_PER_MINUTE;
        }

        public synchronized void increment() {
            count++;
        }
    }
} 