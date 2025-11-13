package ru.example.notificationservice.dto;


import java.time.Instant;

public record DlqMessage(
          Object originalMessage,
          String originalTopic,
          String errorReason,
          Instant timestamp,
          String consumerThread
) {}