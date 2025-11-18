package ru.example.notificationservice.manager;

import ru.example.notificationservice.dto.NotificationDto;

public interface NotificationManager {

    void handleEventByHandler(NotificationDto notificationDtoEvent);
}
