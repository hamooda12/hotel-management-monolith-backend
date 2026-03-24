package com.example.hotalproject.HotelCatalog.notification;

import com.example.hotalproject.security.SecurityUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
@Tag(name = "Notification", description = "Notification inbox endpoints")
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping
    @Operation(summary = "Get notifications for authenticated user")
    public ResponseEntity<List<NotificationResponse>> getByRecipient() {
        return ResponseEntity.ok(notificationService.getByRecipient(SecurityUtils.currentUserEmail()));
    }
}