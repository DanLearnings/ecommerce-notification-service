package com.danlearnings.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NotificationEvent implements Serializable {
    private String eventType;
    private String recipient;
    private String subject;
    private String body;
    private String relatedEntityType;
    private String relatedEntityId;
}