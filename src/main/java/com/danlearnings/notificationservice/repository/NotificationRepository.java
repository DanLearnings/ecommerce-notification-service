package com.danlearnings.notificationservice.repository;

import com.danlearnings.notificationservice.model.Notification;
import com.danlearnings.notificationservice.model.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByRecipient(String recipient);

    List<Notification> findByStatus(NotificationStatus status);

    List<Notification> findByRelatedEntityTypeAndRelatedEntityId(String entityType, String entityId);
}