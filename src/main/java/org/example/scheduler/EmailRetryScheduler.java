package org.example.scheduler;

import lombok.RequiredArgsConstructor;
import org.example.service.EmailService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailRetryScheduler {

    private final EmailService emailService;

    @Scheduled(fixedRate = 300000)
    public void retryFailed() {
        emailService.retryFailedEmails();
    }
}
