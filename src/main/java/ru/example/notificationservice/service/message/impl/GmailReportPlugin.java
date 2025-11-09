package ru.example.notificationservice.service.message.impl;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import ru.example.notificationservice.model.Notification;
import ru.example.notificationservice.service.message.ReportPlugin;
import ru.example.notificationservice.service.template.TemplateService;

@Slf4j
@Service
@RequiredArgsConstructor
public class GmailReportPlugin implements ReportPlugin {

          private final JavaMailSender mailSender;

          private final TemplateService templateService;


          @Override
          public void handle(Notification notification) {
                    MimeMessage emailMessage = templateService.create(notification);

                    mailSender.send(emailMessage);
                    log.info("GmailService| сообщение отправлено на почту");
          }


          @Override
          public boolean supports(String recipientType) {
                    return recipientType.equalsIgnoreCase("EMAIL");
          }
}