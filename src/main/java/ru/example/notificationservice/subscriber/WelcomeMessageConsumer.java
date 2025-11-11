package ru.example.notificationservice.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.example.notificationservice.dto.UserRegisteredPayload;
import ru.example.notificationservice.manager.NotificationManager;
import ru.example.notificationservice.mapper.NotificationMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class WelcomeMessageConsumer {

          private final NotificationManager notificationManager;

          private final ObjectMapper objectMapper;

          private final NotificationMapper notificationMapper;

          @SneakyThrows
          @KafkaListener(topics = "${kafka.topics.email-sending}")
          public void handleOutboxEvent(String payloadJson) {
                    UserRegisteredPayload payload = objectMapper.readValue(parseDebeziumPayload(payloadJson),
                              UserRegisteredPayload.class);

                    notificationManager.handleEventByHandler(
                              notificationMapper.toNotification(payload));
          }

          private String parseDebeziumPayload(String json) {
                    return (json.startsWith("\"") && json.endsWith("\"")
                              ? json.substring(1, json.length() - 1)
                              : json).replace("\\\"", "\"");
          }
}