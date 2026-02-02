package org.example.controller;

import org.example.entity.EmailMessage;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final KafkaTemplate<String, EmailMessage> kafkaTemplate;

    public TestController(KafkaTemplate<String, EmailMessage> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @PostMapping("/test-send")
    public String sendTest() {
        EmailMessage msg = new EmailMessage();
        msg.setSubject("ТТест");
        msg.setContent("Перевірка");
        msg.setRecipient("nikorzhe@gmail.com");

        kafkaTemplate.send("email-queue", msg);
        return "Повідомлення у Kafka";
    }
}
