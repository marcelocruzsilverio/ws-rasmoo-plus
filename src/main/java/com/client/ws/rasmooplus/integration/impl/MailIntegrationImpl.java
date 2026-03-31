package com.client.ws.rasmooplus.integration.impl;

import com.client.ws.rasmooplus.integration.MailIntegration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

@Component
public class MailIntegrationImpl implements MailIntegration {

    private final JavaMailSender mailSender;


    public MailIntegrationImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void send(String mailTo, String message,  String subject) {
        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(mailTo);
        mailMessage.setSubject(subject);
        mailMessage.setText(message);

        mailSender.send(mailMessage);
    }
}
