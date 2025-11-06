package ru.example.notificationservice.service;

import lombok.RequiredArgsConstructor;
import org.springframework.plugin.core.PluginRegistry;
import org.springframework.stereotype.Service;
import ru.example.notificationservice.model.Notification;
import ru.example.notificationservice.service.message.ReportPlugin;
import ru.example.notificationservice.service.template.TemplateService;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

          private final PluginRegistry<ReportPlugin, String> pluginRegistry;

          private final TemplateService templateService;

          public void handleEventByHandler(Notification notificationEvent) {

                    pluginRegistry.getPluginsFor(notificationEvent.recipientType())
                              .forEach(handler -> handler.handle(notificationEvent));
          }


}
