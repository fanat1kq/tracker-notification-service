//package ru.example.notificationservice.kafka;
//
//import lombok.RequiredArgsConstructor;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.kafka.annotation.KafkaListener;
//import ru.example.notificationservice.manager.NotificationManager;
//import ru.example.notificationservice.model.Notification;
//
//import java.util.function.Consumer;
//
//@Configuration
//@RequiredArgsConstructor
//public class NotificationConsumer {
//
//    private final NotificationManager notificationManager;
//
//    @KafkaListener(topics = "${kafka.topics.user-info}",
//              groupId = "${spring.kafka.consumer.group-id}")
//    public void handleEvent(UserInformationDto messageDto) {
//        return notificationManager::handleEventByHandler;
//    }
//}
