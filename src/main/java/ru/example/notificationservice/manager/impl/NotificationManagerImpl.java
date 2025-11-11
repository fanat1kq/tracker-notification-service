package ru.example.notificationservice.manager.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Service;
import ru.example.notificationservice.dto.Notification;
import ru.example.notificationservice.manager.NotificationManager;
import ru.example.notificationservice.service.message.ReportPlugin;


@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationManagerImpl implements NotificationManager {

          private final PluginRegistry<ReportPlugin, String> pluginRegistry;

          @Override
          public void handleEventByHandler(
                    Notification notificationEvent) {
                    pluginRegistry.getPluginsFor(notificationEvent.recipientType())
                              .forEach(handler -> handler.handle(notificationEvent));
          }
}
