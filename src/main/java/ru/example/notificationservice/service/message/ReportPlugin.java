package ru.example.notificationservice.service.message;

import org.springframework.plugin.core.Plugin;
import ru.example.notificationservice.dto.NotificationDto;

public interface ReportPlugin extends Plugin<String> {

    void handle(NotificationDto notificationDto);
}