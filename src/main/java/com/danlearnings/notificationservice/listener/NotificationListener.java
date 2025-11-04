package com.danlearnings.notificationservice.listener;

import com.danlearnings.notificationservice.config.RabbitMQConfig;
import com.danlearnings.notificationservice.dto.NotificationEvent;
import com.danlearnings.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class NotificationListener {

    private final NotificationService notificationService;

    @RabbitListener(queues = RabbitMQConfig.NOTIFICATION_QUEUE)
    public void handleNotificationEvent(NotificationEvent event) {
        log.info("📨 Received notification event from RabbitMQ: {}", event);

        try {
            notificationService.sendEmail(
                    event.getRecipient(),
                    event.getSubject(),
                    event.getBody(),
                    event.getRelatedEntityType(),
                    event.getRelatedEntityId()
            );

            log.info("✅ Email sent successfully for event: {}", event.getEventType());

        } catch (Exception e) {
            log.error("❌ Failed to send email for event: {} | Error: {}", event.getEventType(), e.getMessage());
        }
    }
}