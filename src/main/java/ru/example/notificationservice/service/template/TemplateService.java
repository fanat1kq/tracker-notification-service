package ru.example.notificationservice.service.template;

import jakarta.mail.internet.MimeMessage;
import ru.example.notificationservice.dto.NotificationDto;

public interface TemplateService {

    MimeMessage create(NotificationDto notificationDto);
}

