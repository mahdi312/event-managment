package com.event.management.notificationservice.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mail.javamail.JavaMailSender;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void sendEmail_shouldUseMailSender() {
        doNothing().when(mailSender).send(any(org.springframework.mail.SimpleMailMessage.class));
        emailService.sendEmail("mahdi.mostafavi312@gmail.com", "Subj", "Body");
        verify(mailSender).send(any(org.springframework.mail.SimpleMailMessage.class));
    }
}


