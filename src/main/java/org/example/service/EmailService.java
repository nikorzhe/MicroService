package org.example.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.EmailMessage;
import org.example.repository.EmailMessageRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private final EmailMessageRepository repository;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "email-queue", groupId = "email-group")
    public void receiveMessage(String json) {
        log.info("ОТРИМАНО JSON З KAFKA: {}", json);

        try {
            EmailMessage message = objectMapper.readValue(json, EmailMessage.class);

            message.setStatus("PENDING");
            message.setCreatedAt(LocalDateTime.now());
            message.setAttemptCount(0);

            repository.save(message);
            sendEmail(message);
        } catch (Exception e) {
            log.error("ПОМИЛКА ОБРОБКИ ПОВІДОМЛЕННЯ: {}", e.getMessage());
        }
    }

    public void sendEmail(EmailMessage message) {
        try {
            SimpleMailMessage mail = new SimpleMailMessage();
            mail.setTo(message.getRecipient());
            mail.setSubject(message.getSubject());
            mail.setText(message.getContent());

            mailSender.send(mail);

            message.setStatus("SENT");
            message.setLastAttemptTime(LocalDateTime.now());
            repository.save(message);
            log.info("Email успішно надіслано на {}", message.getRecipient());

        } catch (Exception e) {
            message.setStatus("FAILED");
            message.setErrorMessage(e.getMessage());
            message.setLastAttemptTime(LocalDateTime.now());
            message.setAttemptCount(message.getAttemptCount() + 1);
            repository.save(message);
            log.error("Помилка відправки на {}: {}", message.getRecipient(), e.getMessage());
        }
    }

    public void retryFailedEmails() {
        LocalDateTime threshold = LocalDateTime.now().minusMinutes(5);
        List<EmailMessage> failed = repository.findByStatusAndLastAttemptTimeBefore("FAILED", threshold);
        failed.forEach(this::sendEmail);
    }
}