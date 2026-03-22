package com.example.hotalproject.HotelCatalog.notification;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationResponse {
    private Long id;
    private String recipient;
    private NotificationType type;
    private String subject;
    private String message;
    private NotificationStatus status;
    private LocalDateTime createdAt;
}