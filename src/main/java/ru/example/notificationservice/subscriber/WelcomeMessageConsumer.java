package ru.example.notificationservice.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import ru.example.notificationservice.dto.NotificationDto;
import ru.example.notificationservice.dto.UserRegisteredPayload;
import ru.example.notificationservice.exception.DuplicateMessageException;
import ru.example.notificationservice.manager.NotificationManager;
import ru.example.notificationservice.mapper.NotificationMapper;

@Service
@Slf4j
@RequiredArgsConstructor
public class WelcomeMessageConsumer {

    private final NotificationManager notificationManager;

    private final ObjectMapper objectMapper;

    private final DeduplicationService deduplicationService;

    private final NotificationMapper notificationMapper;

    @SneakyThrows
    @RetryableTopic(
        attempts = "3",
        backoff = @Backoff(delay = 2000, multiplier = 2.0),
        autoCreateTopics = "false"
    )
    @KafkaListener(topics = "${kafka.topics.outbox}")
    public void handleOutboxEvent(ConsumerRecord<String, String> outboxRecord,
                                  Acknowledgment ack) {
        try {
            deduplicationService.executeWithDeduplication(
                outboxRecord.key(),
                () -> processNotification(outboxRecord.value()));
            ack.acknowledge();
        } catch (DuplicateMessageException e) {
            ack.acknowledge();
        }
    }

    @DltHandler
    public void handleOutboxDlt(String payloadJson,
                                @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                @Header(KafkaHeaders.EXCEPTION_MESSAGE)
                                String exceptionMessage) {
        log.error("Outbox message reached DLT. Topic: {}, Error: {}, Payload: {}",
            topic, exceptionMessage, payloadJson);
    }

    @SneakyThrows
    private void processNotification(String recordValue) {
        String debeziumPayload = parseDebeziumPayload(recordValue);
        UserRegisteredPayload payload =
            objectMapper.readValue(debeziumPayload, UserRegisteredPayload.class);

        log.info("Processing welcome message for user: {}", payload.email());

        NotificationDto notificationDto = notificationMapper.toNotification(payload);
        notificationManager.handleEventByHandler(notificationDto);
    }

    private String parseDebeziumPayload(String json) {
        return (json.startsWith("\"") && json.endsWith("\"")
            ? json.substring(1, json.length() - 1)
            : json).replace("\\\"", "\"");
    }
}