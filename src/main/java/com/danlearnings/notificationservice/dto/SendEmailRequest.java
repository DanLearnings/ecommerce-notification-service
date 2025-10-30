package com.danlearnings.notificationservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SendEmailRequest {
    private String recipient;
    private String subject;
    private String body;
    private String relatedEntityType;
    private String relatedEntityId;
}