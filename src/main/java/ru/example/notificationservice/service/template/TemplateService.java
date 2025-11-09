package ru.example.notificationservice.service.template;

import jakarta.mail.internet.MimeMessage;
import ru.example.notificationservice.model.Notification;

/**
 * Interface for a service that handles template processing.
 * This service is responsible for processing text templates with variable data,
 * utilizing a templating engine like Thymeleaf.
 */
public interface TemplateService {

          MimeMessage create(Notification notification);
}

