package com.example.uf_spring.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class LoggingInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String method = request.getMethod();
        String uri = request.getRequestURI();
        String queryString = request.getQueryString();
        String userAgent = request.getHeader("User-Agent");
        String remoteAddr = request.getRemoteAddr();
        
        log.info("=== HTTP 요청 시작 ===");
        log.info("Method: {}", method);
        log.info("URI: {}", uri);
        if (queryString != null) {
            log.info("Query: {}", queryString);
        }
        log.info("User-Agent: {}", userAgent);
        log.info("Remote Address: {}", remoteAddr);
        log.info("=====================");
        
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        int status = response.getStatus();
        String method = request.getMethod();
        String uri = request.getRequestURI();
        
        log.info("=== HTTP 요청 완료 ===");
        log.info("Method: {}", method);
        log.info("URI: {}", uri);
        log.info("Status: {}", status);
        if (ex != null) {
            log.error("Exception: {}", ex.getMessage());
        }
        log.info("=====================");
    }
} 