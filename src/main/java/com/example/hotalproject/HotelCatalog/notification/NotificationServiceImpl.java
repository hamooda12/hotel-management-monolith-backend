package com.example.hotalproject.HotelCatalog.notification;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public NotificationResponse send(String recipient,
                                     NotificationType type,
                                     String subject,
                                     String message) {

        Notification notification = Notification.builder()
                .recipient(recipient)
                .type(type)
                .subject(subject)
                .message(message)
                .status(NotificationStatus.SENT)
                .build();

        notification = notificationRepository.save(notification);

        log.info("Mock notification sent to {} | type={} | subject={}",
                recipient, type, subject);

        return toResponse(notification);
    }

    @Override
    public List<NotificationResponse> getByRecipient(String recipient) {
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(recipient)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private NotificationResponse toResponse(Notification notification) {
        return NotificationResponse.builder()
                .id(notification.getId())
                .recipient(notification.getRecipient())
                .type(notification.getType())
                .subject(notification.getSubject())
                .message(notification.getMessage())
                .status(notification.getStatus())
                .createdAt(notification.getCreatedAt())
                .build();
    }
}