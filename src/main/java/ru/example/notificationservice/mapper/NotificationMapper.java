package ru.example.notificationservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.example.notificationservice.dto.EmailMessage;
import ru.example.notificationservice.dto.Notification;
import ru.example.notificationservice.dto.NotificationType;
import ru.example.notificationservice.dto.UserRegisteredPayload;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

          @Mapping(source = "email", target = "to")
          Notification toNotification(UserRegisteredPayload payload);

          @Mapping(target = "subject", expression = "java(resolveSubject(notification))")
          EmailMessage toEmailMessage(Notification notification);

          default String resolveSubject(Notification notification) {
                    NotificationType emailType = NotificationType.fromString(notification.templateType());
                    return notification.data().getOrDefault("subject", emailType.getDefaultSubject());
          }
}