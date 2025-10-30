package com.danlearnings.notificationservice.controller;

import com.danlearnings.notificationservice.dto.SendEmailRequest;
import com.danlearnings.notificationservice.dto.TestEmailRequest;
import com.danlearnings.notificationservice.model.Notification;
import com.danlearnings.notificationservice.model.NotificationStatus;
import com.danlearnings.notificationservice.service.EmailTemplateService;
import com.danlearnings.notificationservice.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/notifications")
@RequiredArgsConstructor
@Slf4j
public class NotificationController {

    private final NotificationService notificationService;
    private final EmailTemplateService emailTemplateService;

    @PostMapping("/test")
    public ResponseEntity<Notification> sendTestEmail(@RequestBody TestEmailRequest request) {
        log.info("Received test email request for: {}", request.getRecipient());

        String subject = "🎉 Test Email - E-commerce Notification Service";
        String body = emailTemplateService.getTestEmailBody(request.getName());

        Notification notification = notificationService.sendEmail(
                request.getRecipient(),
                subject,
                body,
                "TEST",
                "test-001"
        );

        return ResponseEntity.ok(notification);
    }

    @PostMapping("/send")
    public ResponseEntity<Notification> sendEmail(@RequestBody SendEmailRequest request) {
        log.info("Received email request for: {}", request.getRecipient());

        Notification notification = notificationService.sendEmail(
                request.getRecipient(),
                request.getSubject(),
                request.getBody(),
                request.getRelatedEntityType(),
                request.getRelatedEntityId()
        );

        return ResponseEntity.ok(notification);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Notification> getNotificationById(@PathVariable Long id) {
        Notification notification = notificationService.getNotificationById(id);
        return ResponseEntity.ok(notification);
    }

    @GetMapping("/recipient/{email}")
    public ResponseEntity<List<Notification>> getNotificationsByRecipient(@PathVariable String email) {
        List<Notification> notifications = notificationService.getNotificationsByRecipient(email);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping
    public ResponseEntity<List<Notification>> getAllNotifications() {
        List<Notification> notifications = notificationService.getAllNotifications();
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<Notification>> getNotificationsByStatus(@PathVariable NotificationStatus status) {
        List<Notification> notifications = notificationService.getNotificationsByStatus(status);
        return ResponseEntity.ok(notifications);
    }
}