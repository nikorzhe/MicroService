package org.example.dto;

import lombok.Data;

@Data
public class EmailNotificationDTO {
    private String subject;
    private String content;
    private String recipient;
}
