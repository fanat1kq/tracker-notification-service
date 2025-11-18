package ru.example.notificationservice.dto;

import lombok.Builder;

import java.util.Map;

@Builder
public record NotificationDto(
    String to,
    String templateType,
    String recipientType,
    Map<String, String> data
) {
}
