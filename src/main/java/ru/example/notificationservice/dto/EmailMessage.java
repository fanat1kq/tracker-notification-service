package ru.example.notificationservice.dto;

public record EmailMessage(String to, String subject, String htmlContent) {
    public EmailMessage withHtmlContent(String htmlContent) {
        return new EmailMessage(to, subject, htmlContent);
    }
}