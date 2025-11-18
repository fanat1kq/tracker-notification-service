package ru.example.notificationservice.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import ru.example.notificationservice.dto.NotificationDto;
import ru.example.notificationservice.exception.DuplicateMessageException;
import ru.example.notificationservice.manager.NotificationManager;

@Service
@Slf4j
@RequiredArgsConstructor
public class TaskNotificationConsumer {

    private final NotificationManager notificationManager;

    private final ObjectMapper objectMapper;

    private final DeduplicationService deduplicationService;

    @RetryableTopic(
        attempts = "6",
        backoff = @Backoff(
            delay = 1000,
            multiplier = 2.0,
            maxDelay = 30000,
            random = true
        ),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
    )
    @KafkaListener(topics = "${kafka.topics.email-sending}")
    public void consume(ConsumerRecord<String, String> emailRecord,
                        Acknowledgment ack) {
        try {
            deduplicationService.executeWithDeduplication(
                emailRecord.key(),
                () -> processNotification(emailRecord.value()));
            ack.acknowledge();
        } catch (
            DuplicateMessageException e) {
            ack.acknowledge();
        }
    }

    @DltHandler
    public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
        log.error("Message: {} reached DLT from topic: {}", message, topic);
    }

    @SneakyThrows
    private void processNotification(String message) {
        NotificationDto notificationDto =
            objectMapper.readValue(message, NotificationDto.class);
        log.info("Received task notification = {}", notificationDto);

        notificationManager.handleEventByHandler(notificationDto);
    }
}