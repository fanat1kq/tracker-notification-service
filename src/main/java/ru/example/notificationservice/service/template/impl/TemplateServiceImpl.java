package ru.example.notificationservice.service.template.impl;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.example.notificationservice.model.EmailMessage;
import ru.example.notificationservice.model.Notification;
import ru.example.notificationservice.model.NotificationType;
import ru.example.notificationservice.service.template.TemplateService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

          private final JavaMailSender mailSender;

          private final SpringTemplateEngine templateEngine;
          @Value("${spring.mail.username}")
          private String username;

          @Override
          public MimeMessage create(Notification notification) {
                    NotificationType emailType =
                              NotificationType.fromString(notification.templateType());
                    Map<String, String> params = notification.data();
                    String subject = params.getOrDefault("subject", emailType.getDefaultSubject());

                    EmailMessage email = EmailMessage.builder()
                              .to(notification.to())
                              .subject(subject)
                              .htmlContent(renderTemplate(emailType, params))
                              .build();

                    return convertToMimeMessage(email);
          }

          private String renderTemplate(NotificationType notificationType,
                                        Map<String, String> params) {
                    Context context = new Context();
                    params.forEach(context::setVariable);
                    return templateEngine.process(notificationType.getTemplateName(), context);
          }

          private MimeMessage convertToMimeMessage(EmailMessage email) {
                    MimeMessage mimeMessage = mailSender.createMimeMessage();

                    try {
                              MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                              helper.setTo(email.getTo());
                              helper.setSubject(email.getSubject());
                              helper.setText(email.getHtmlContent(), true);
                              helper.setFrom(username);
                    } catch (MessagingException e) {
                              throw new RuntimeException("Failed to send email", e);
                    }


                    return mimeMessage;
          }
}
