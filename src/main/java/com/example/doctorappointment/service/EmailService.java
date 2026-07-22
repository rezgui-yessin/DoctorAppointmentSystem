package com.example.doctorappointment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender javaMailSender;

    public void sendReminderEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            message.setFrom("noreply@doctorappointment.com");
            
            javaMailSender.send(message);
            log.info("Reminder email sent to: {}", to);
        } catch (Exception e) {
            log.error("Failed to send email to: {}", to, e);
        }
    }
}
