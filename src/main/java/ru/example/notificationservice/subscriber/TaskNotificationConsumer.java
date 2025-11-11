package ru.example.notificationservice.subscriber;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import ru.example.notificationservice.dto.Notification;
import ru.example.notificationservice.manager.NotificationManager;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskNotificationConsumer {
          private final NotificationManager notificationManager;
          private final ObjectMapper objectMapper;

          @SneakyThrows
          @KafkaListener(topics = "${kafka.topics.email-sending}")
          public void consume(String message) {
                    Notification dto = objectMapper.readValue(message, Notification.class);
                    log.info("Received task = {}", dto);
                    notificationManager.handleEventByHandler(dto);
          }
}
