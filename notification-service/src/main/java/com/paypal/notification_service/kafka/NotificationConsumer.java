package com.paypal.notification_service.kafka;

import com.paypal.notification_service.repository.NotificationRepository;
import org.springframework.stereotype.Component;

@Component
public class NotificationConsumer {
    private final NotificationRepository notificationRepository;

    public NotificationConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
}
