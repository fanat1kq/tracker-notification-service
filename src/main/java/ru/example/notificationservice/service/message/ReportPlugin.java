package ru.example.notificationservice.service.message;

import org.springframework.plugin.core.Plugin;
import ru.example.notificationservice.dto.Notification;

public interface ReportPlugin extends Plugin<String> {

          void handle(Notification notification);
}