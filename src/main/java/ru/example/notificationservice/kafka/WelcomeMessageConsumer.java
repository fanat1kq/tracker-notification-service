package ru.example.notificationservice.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.retry.annotation.Backoff;
import org.springframework.stereotype.Service;
import ru.example.notificationservice.dto.EmailMessageRequestDto;
import ru.example.notificationservice.manager.NotificationManager;
import ru.example.notificationservice.model.Notification;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class WelcomeMessageConsumer {

private final NotificationManager notificationManager;


    @KafkaListener(
            topics = "${kafka.topics.email-sending}"
//            groupId = "${spring.kafka.consumer.group-id}"
    )
//    @RetryableTopic(
//              attempts = "3",  // 3 попытки перед отправкой в DLT
//              backoff = @Backoff(delay = 1000, multiplier = 2.0),
//              autoCreateTopics = "true",
//              topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE
//    )
    public void consume(Notification messageDto) {
        log.info("Get message = {}", messageDto.toString());
//        Notification build = Notification.builder()
//                  .recipientType("EMAIL")
//                  .to("fanat1kq11@yandex.ru")
//                  .templateType("TASK_REPORT")
//                  .data(Map.of("completedTasks", "2", "inProgressTasks", "1","pendingCount","4"))
//                  .build();

        int a =1;
        notificationManager.handleEventByHandler(messageDto);
//        if (!"fail@example.com".equals(messageDto.to())) {
//            throw new RuntimeException("Simulated business error for DLT testing");
//        }
//        emailService.sendEmail(messageDto);
    }
//    @DltHandler
//    public void handleDlt(EmailMessageRequestDto failedMessage) {
//        System.err.println("💀 MESSAGE MOVED TO DLT: " + failedMessage);
//        // Логируем, сохраняем в БД, отправляем уведомление
//    }

}
