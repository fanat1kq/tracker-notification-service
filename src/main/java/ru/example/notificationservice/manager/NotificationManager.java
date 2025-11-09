package ru.example.notificationservice.manager;


import ru.example.notificationservice.model.Notification;

public interface NotificationManager {

    void handleEventByHandler(Notification notificationEvent);
}
