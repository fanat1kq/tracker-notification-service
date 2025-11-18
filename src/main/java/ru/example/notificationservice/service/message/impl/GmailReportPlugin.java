package ru.example.notificationservice.service.message.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.example.notificationservice.dto.NotificationDto;
import ru.example.notificationservice.dto.enumurates.RecipientType;
import ru.example.notificationservice.service.message.ReportPlugin;
import ru.example.notificationservice.service.template.TemplateService;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailReportPlugin implements ReportPlugin {

    private final JavaMailSender mailSender;

    private final TemplateService templateService;


    @Override
    public void handle(NotificationDto notificationDto) {
        MimeMessage emailMessage = templateService.create(notificationDto);

        mailSender.send(emailMessage);
        log.info("Cообщение отправлено на почту");
    }

    @Override
    public boolean supports(String recipientType) {
        return recipientType.equalsIgnoreCase(RecipientType.EMAIL.getRecipientName());
    }
}