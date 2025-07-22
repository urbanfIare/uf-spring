package com.example.uf_spring.controller;

import com.example.uf_spring.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notify")
public class NotificationController {
    @Autowired
    private NotificationService notificationService;

    @PostMapping
    public ResponseEntity<Void> send(@RequestParam String message) {
        notificationService.sendNotification(message);
        return ResponseEntity.ok().build();
    }
} 