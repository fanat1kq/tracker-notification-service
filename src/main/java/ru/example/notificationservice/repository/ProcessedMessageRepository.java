package ru.example.notificationservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.example.notificationservice.entity.ProcessedMessage;

import java.util.Optional;

public interface ProcessedMessageRepository extends JpaRepository<ProcessedMessage, String> {
    Optional<ProcessedMessage> findByDeduplicationKey(String deduplicationKey);

    void deleteByDeduplicationKey(String deduplicationKey);
}