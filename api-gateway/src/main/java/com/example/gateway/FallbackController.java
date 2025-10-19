package com.example.gateway;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class FallbackController {

    @RequestMapping("/fallback/user-service")
    public ResponseEntity<String> userFallback() {
        return ResponseEntity.status(503).body("User service is temporarily unavailable. Please try later.");
    }

    @RequestMapping("/fallback/notification-service")
    public ResponseEntity<String> notificationFallback() {
        return ResponseEntity.status(503).body("Notification service is temporarily unavailable. Please try later.");
    }
}
