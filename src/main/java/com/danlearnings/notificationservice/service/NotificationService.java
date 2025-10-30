package com.danlearnings.notificationservice.service;

import com.danlearnings.notificationservice.model.Notification;
import com.danlearnings.notificationservice.model.NotificationStatus;
import com.danlearnings.notificationservice.model.NotificationType;
import com.danlearnings.notificationservice.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationService {

    private final JavaMailSender mailSender;
    private final NotificationRepository notificationRepository;

    @Transactional
    public Notification sendEmail(String to, String subject, String body) {
        return sendEmail(to, subject, body, null, null);
    }

    @Transactional
    public Notification sendEmail(String to, String subject, String body,
                                  String relatedEntityType, String relatedEntityId) {
        log.info("Sending email to: {} | Subject: {}", to, subject);

        // Criar notificação
        Notification notification = Notification.builder()
                .type(NotificationType.EMAIL)
                .recipient(to)
                .subject(subject)
                .body(body)
                .status(NotificationStatus.PENDING)
                .relatedEntityType(relatedEntityType)
                .relatedEntityId(relatedEntityId)
                .attempts(0)
                .build();

        try {
            // Enviar email
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(body);
            message.setFrom("noreply@ecommerce-ecosystem.com");

            mailSender.send(message);

            // Atualizar status
            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());
            notification.setAttempts(1);

            log.info("Email sent successfully to: {}", to);

        } catch (Exception e) {
            log.error("Failed to send email to: {} | Error: {}", to, e.getMessage());
            notification.setStatus(NotificationStatus.FAILED);
            notification.setAttempts(1);
        }

        // Salvar histórico
        return notificationRepository.save(notification);
    }

    public Notification getNotificationById(Long id) {
        return notificationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notification not found with id: " + id));
    }

    public List<Notification> getNotificationsByRecipient(String recipient) {
        return notificationRepository.findByRecipient(recipient);
    }

    public List<Notification> getAllNotifications() {
        return notificationRepository.findAll();
    }

    public List<Notification> getNotificationsByStatus(NotificationStatus status) {
        return notificationRepository.findByStatus(status);
    }

    public List<Notification> getNotificationsByEntity(String entityType, String entityId) {
        return notificationRepository.findByRelatedEntityTypeAndRelatedEntityId(entityType, entityId);
    }
}