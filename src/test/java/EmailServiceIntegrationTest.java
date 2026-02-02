import org.example.EmailServiceApplication;
import org.example.dto.EmailNotificationDTO;
import org.example.entity.EmailMessage;
import org.example.repository.EmailMessageRepository;
import org.example.service.EmailService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.ActiveProfiles;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest(
        classes = EmailServiceApplication.class,
        properties = {
                "spring.kafka.bootstrap-servers=localhost:9092",
                "spring.elasticsearch.uris=http://localhost:9200",
                "spring.main.allow-bean-definition-overriding=true"
        }
)
@ActiveProfiles("test")
class EmailServiceIntegrationTest {

    @Autowired
    private EmailService emailService;

    @MockBean
    private JavaMailSender mailSender;

    @MockBean
    private EmailMessageRepository emailMessageRepository;

    @MockBean
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void successfulEmailSend() {
        EmailMessage message = new EmailMessage();
        message.setRecipient("test@example.com");
        message.setSubject("Test");
        message.setContent("Hello");

        doNothing().when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(message);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }

    @Test
    void failedEmailSend() {
        EmailMessage message = new EmailMessage();
        message.setRecipient("test@example.com");
        message.setSubject("Test");
        message.setContent("Hello");

        doThrow(new RuntimeException("SMTP error"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        emailService.sendEmail(message);

        verify(mailSender, times(1)).send(any(SimpleMailMessage.class));
    }
}