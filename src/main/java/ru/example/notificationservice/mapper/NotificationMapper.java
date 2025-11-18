package ru.example.notificationservice.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.example.notificationservice.dto.EmailMessage;
import ru.example.notificationservice.dto.NotificationDto;
import ru.example.notificationservice.dto.UserRegisteredPayload;
import ru.example.notificationservice.dto.enumurates.NotificationType;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "email", target = "to")
    NotificationDto toNotification(UserRegisteredPayload payload);

    @Mapping(target = "subject", expression = "java(resolveSubject(notificationDto))")
    EmailMessage toEmailMessage(NotificationDto notificationDto);

    default String resolveSubject(NotificationDto notificationDto) {
        NotificationType emailType = NotificationType.fromString(notificationDto.templateType());
        return notificationDto.data().getOrDefault("subject", emailType.getDefaultSubject());
    }
}