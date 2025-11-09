package ru.example.notificationservice.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Component;


@Component
public class NotificationFactoryImpl implements NotificationFactory {

    @Value("${spring.mail.username}")
    private String sender;

    @Override
    public SimpleMailMessage createMailNotification(String recipientData, String message) {
        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();

        simpleMailMessage.setText(message);
        simpleMailMessage.setFrom(sender);
        simpleMailMessage.setTo(recipientData);
        return simpleMailMessage;
    }
}
