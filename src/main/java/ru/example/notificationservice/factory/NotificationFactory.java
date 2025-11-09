package ru.example.notificationservice.factory;

import org.springframework.mail.SimpleMailMessage;



public interface NotificationFactory {

    SimpleMailMessage createMailNotification(String recipientType, String text);
}

