package com.example.service;

import com.example.model.UserEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class NotificationService {
    private final JavaMailSender mailSender;

    public NotificationService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(text);
        mailSender.send(msg);
        log.info("Письмо отправлено на {}", to);
    }

    @KafkaListener(topics = "user-events", groupId = "notification-service")
    public void handleUserEvent(UserEvent event) {
        log.info("Получено событие {} для {}", event.operation(), event.email());
        switch (event.operation()) {
            case "CREATE" -> sendEmail(event.email(),
                    "Добро пожаловать!",
                    "Здравствуйте! Ваш аккаунт на сайте был успешно создан.");
            case "DELETE" -> sendEmail(event.email(),
                    "Аккаунт удалён",
                    "Здравствуйте! Ваш аккаунт был удалён.");
            default -> {
                log.warn("Неизвестная операция {}", event.operation());

            }
        }
    }
}