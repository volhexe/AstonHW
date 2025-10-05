package com.example.controller;

import com.example.model.UserEvent;
import com.example.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    public record ManualNotificationRequest(String email, String operation) {}

    @PostMapping("/send")
    public ResponseEntity<String> sendNotification(@RequestBody ManualNotificationRequest req) {
        if (req.email() == null || req.email().isBlank() || req.operation() == null || req.operation().isBlank()) {
            return ResponseEntity.badRequest().body("Email обязательн");
        }

        notificationService.handleUserEvent(
                new UserEvent(req.operation().toUpperCase(), req.email())
        );
        return ResponseEntity.ok("Письмо отправлено");
    }
}