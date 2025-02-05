package com.example.cookbook.service;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        message.setFrom("testowanie.uph@poczta.o2.pl");

        try {
            mailSender.send(message);
            System.out.println("✅ Email sent successfully to: " + to);
        } catch (MailException e) {
            System.out.println("❌ Error sending email to " + to + ": " + e.getMessage());
        }
    }
}
