package org.example.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@Document(indexName = "email-messages")
public class EmailMessage {
    @Id
    private String id;

    @Field(type = FieldType.Text)
    private String subject;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Keyword)
    private String recipient;

    @Field(type = FieldType.Keyword)
    private String status; // PENDING, SENT, FAILED

    @Field(type = FieldType.Text)
    private String errorMessage;

    @Field(type = FieldType.Integer)
    private int attemptCount = 0;

    @Field(type = FieldType.Date)
    private LocalDateTime lastAttemptTime;

    @Field(type = FieldType.Date)
    private LocalDateTime createdAt = LocalDateTime.now();
}
