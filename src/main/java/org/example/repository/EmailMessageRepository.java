package org.example.repository;

import org.example.entity.EmailMessage;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EmailMessageRepository extends ElasticsearchRepository<EmailMessage, String> {
    List<EmailMessage> findByStatusAndLastAttemptTimeBefore(String status, LocalDateTime time);
}