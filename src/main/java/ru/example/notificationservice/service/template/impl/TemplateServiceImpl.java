package ru.example.notificationservice.service.template.impl;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailPreparationException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;
import ru.example.notificationservice.dto.EmailMessage;
import ru.example.notificationservice.dto.Notification;
import ru.example.notificationservice.dto.NotificationType;
import ru.example.notificationservice.mapper.NotificationMapper;
import ru.example.notificationservice.service.template.TemplateService;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

          private final SpringTemplateEngine templateEngine;
          private final JavaMailSender mailSender;
          private final NotificationMapper notificationMapper;

          @Value("${spring.mail.username}")
          private String username;

          @Override
          public MimeMessage create(Notification notification) {
                    EmailMessage email = notificationMapper.toEmailMessage(notification);
                    return convertToMimeMessage(email.withHtmlContent(renderTemplate(notification)));
          }

          private String renderTemplate(Notification notification) {
                    Context context = new Context();
                    notification.data().forEach(context::setVariable);
                    return templateEngine.process(
                              NotificationType.fromString(notification.templateType()).getTemplateName(),
                              context
                    );
          }

          private MimeMessage convertToMimeMessage(EmailMessage email) {
                    try {
                              MimeMessage mimeMessage = mailSender.createMimeMessage();
                              MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
                              helper.setTo(email.to());
                              helper.setSubject(email.subject());
                              helper.setText(email.htmlContent(), true);
                              helper.setFrom(username);
                              return mimeMessage;
                    } catch (MessagingException e) {
                              throw new MailPreparationException("Failed to create email for: " + email.to(), e);
                    }
          }
}