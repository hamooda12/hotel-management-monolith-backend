package com.example.hotalproject.HotelCatalog.notification;

import java.util.List;

public interface NotificationService {

    NotificationResponse send(String recipient,
                              NotificationType type,
                              String subject,
                              String message);

    List<NotificationResponse> getByRecipient(String recipient);
}