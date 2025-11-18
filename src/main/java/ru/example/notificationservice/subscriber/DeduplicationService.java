package ru.example.notificationservice.subscriber;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.example.notificationservice.entity.ProcessedMessage;
import ru.example.notificationservice.exception.DuplicateMessageException;
import ru.example.notificationservice.repository.ProcessedMessageRepository;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeduplicationService {

    private final ProcessedMessageRepository processedMessageRepository;

    @Transactional
    public void executeWithDeduplication(String deduplicationKey, Runnable businessLogic) {
        if (!tryInsertRecord(deduplicationKey)) {
            throw new DuplicateMessageException(deduplicationKey);
        }

        try {
            businessLogic.run();
        } catch (Exception e) {
            processedMessageRepository.deleteByDeduplicationKey(deduplicationKey);
            log.error("Business logic failed, deduplication record removed for key: {}",
                deduplicationKey);
            throw e;
        }
    }

    private boolean tryInsertRecord(String deduplicationKey) {
        try {
            ProcessedMessage message =
                new ProcessedMessage(deduplicationKey, LocalDateTime.now());
            processedMessageRepository.save(message);
            return true;
        } catch (DataIntegrityViolationException e) {
            return false;
        }
    }
}