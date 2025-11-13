package ru.example.notificationservice.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import ru.example.notificationservice.dto.Notification;
import ru.example.notificationservice.dto.UserRegisteredPayload;
import ru.example.notificationservice.exception.NotificationProcessingException;
import ru.example.notificationservice.manager.NotificationManager;
import ru.example.notificationservice.mapper.NotificationMapper;

@Service
@Slf4j
public class WelcomeMessageConsumer {

          private final NotificationManager notificationManager;

          private final ObjectMapper objectMapper;

          private final NotificationMapper notificationMapper;

          public WelcomeMessageConsumer(NotificationManager notificationManager,
                                        ObjectMapper objectMapper,
                                        NotificationMapper notificationMapper) {
                    this.notificationManager = notificationManager;
                    this.objectMapper = objectMapper;
                    this.notificationMapper = notificationMapper;
          }

          @SneakyThrows
          @RetryableTopic(
                    attempts = "3",
                    backoff = @Backoff(delay = 2000, multiplier = 2.0),
                    autoCreateTopics = "false",
                    include = {
                              Exception.class,
                              NotificationProcessingException.class
                    }
          )
          @KafkaListener(topics = "${kafka.topics.outbox}")
          public void handleOutboxEvent(String payloadJson) {
                    String debeziumPayload = parseDebeziumPayload(payloadJson);
                    UserRegisteredPayload payload =
                              objectMapper.readValue(debeziumPayload, UserRegisteredPayload.class);

                    log.info("Received welcome message for user: {}", payload.email());

                    Notification notification = notificationMapper.toNotification(payload);
                    notificationManager.handleEventByHandler(notification);
          }

          @DltHandler
          public void handleOutboxDlt(String payloadJson,
                                      @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
                                      @Header(KafkaHeaders.EXCEPTION_MESSAGE)
                                      String exceptionMessage) {
                    log.error("Outbox message reached DLT. Topic: {}, Error: {}, Payload: {}",
                              topic, exceptionMessage, payloadJson);
          }

          private String parseDebeziumPayload(String json) {
                    return (json.startsWith("\"") && json.endsWith("\"")
                              ? json.substring(1, json.length() - 1)
                              : json).replace("\\\"", "\"");
          }
}