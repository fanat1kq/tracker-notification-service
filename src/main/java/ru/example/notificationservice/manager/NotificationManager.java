package ru.example.notificationservice.manager;

import ru.example.notificationservice.dto.Notification;

public interface NotificationManager {

    void handleEventByHandler(Notification notificationEvent);
}
