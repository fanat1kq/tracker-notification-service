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
import ru.example.notificationservice.dto.NotificationDto;
import ru.example.notificationservice.dto.enumurates.NotificationType;
import ru.example.notificationservice.mapper.NotificationMapper;
import ru.example.notificationservice.service.template.TemplateService;

@Service
@RequiredArgsConstructor
public class TemplateServiceImpl implements TemplateService {

    public static final String UTF_8 = "UTF-8";

    private final SpringTemplateEngine templateEngine;

    private final JavaMailSender mailSender;

    private final NotificationMapper notificationMapper;
    @Value("${spring.mail.username}")
    private String username;

    @Override
    public MimeMessage create(NotificationDto notificationDto) {
        EmailMessage email = notificationMapper.toEmailMessage(notificationDto);
        return convertToMimeMessage(email.withHtmlContent(renderTemplate(notificationDto)));
    }

    private String renderTemplate(NotificationDto notificationDto) {
        Context context = new Context();
        notificationDto.data().forEach(context::setVariable);
        return templateEngine.process(
            NotificationType.fromString(notificationDto.templateType())
                .getTemplateName(), context);
    }

    private MimeMessage convertToMimeMessage(EmailMessage email) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                new MimeMessageHelper(mimeMessage, true, UTF_8);
            helper.setTo(email.to());
            helper.setSubject(email.subject());
            helper.setText(email.htmlContent(), true);
            helper.setFrom(username);
            return mimeMessage;
        } catch (MessagingException e) {
            throw new MailPreparationException(
                "Failed to create email for: " + email.to(), e);
        }
    }
}