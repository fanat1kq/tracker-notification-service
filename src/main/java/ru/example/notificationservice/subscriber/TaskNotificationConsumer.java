package ru.example.notificationservice.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import ru.example.notificationservice.dto.Notification;
import ru.example.notificationservice.manager.NotificationManager;

@Service
@Slf4j
public class TaskNotificationConsumer {

          private final NotificationManager notificationManager;

          private final ObjectMapper objectMapper;

          public TaskNotificationConsumer(NotificationManager notificationManager,
                                          ObjectMapper objectMapper) {
                    this.notificationManager = notificationManager;
                    this.objectMapper = objectMapper;
          }

          @SneakyThrows
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
          public void consume(String message) {
                    Notification dto = objectMapper.readValue(message, Notification.class);
                    log.info("Received task notification = {}", dto);

                    notificationManager.handleEventByHandler(dto);
          }

          @DltHandler
          public void handleDlt(String message, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic) {
                    log.error("Message: {} reached DLT from topic: {}", message, topic);
          }
}