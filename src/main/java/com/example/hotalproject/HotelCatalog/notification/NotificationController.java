package com.example.hotalproject.HotelCatalog.notification;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    public ResponseEntity<List<NotificationResponse>> getByRecipient(@RequestParam String recipient) {
        return ResponseEntity.ok(notificationService.getByRecipient(recipient));
    }
}