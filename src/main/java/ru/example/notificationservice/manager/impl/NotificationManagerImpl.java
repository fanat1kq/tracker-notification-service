package ru.example.notificationservice.manager.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Service;
import ru.example.notificationservice.dto.NotificationDto;
import ru.example.notificationservice.manager.NotificationManager;
import ru.example.notificationservice.service.message.ReportPlugin;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationManagerImpl implements NotificationManager {

    private final PluginRegistry<ReportPlugin, String> pluginRegistry;

    @Override
    public void handleEventByHandler(
        NotificationDto notificationDtoEvent) {
        pluginRegistry.getPluginsFor(notificationDtoEvent.recipientType())
            .forEach(handler -> handler.handle(notificationDtoEvent));
    }
}
