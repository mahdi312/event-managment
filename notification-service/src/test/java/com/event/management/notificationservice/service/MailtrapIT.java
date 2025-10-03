package com.event.management.notificationservice.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MailtrapIT {

    @Autowired
    private EmailService emailService;

    @Test
    @EnabledIfEnvironmentVariable(named = "MAILTRAP_USERNAME", matches = ".+" )
    void sendEmail_viaMailtrap_shouldSucceed_whenCredentialsProvided() {
        emailService.sendEmail("mahdi.mostafavi312@gmail.com", "Test from CI", "Hello from automated test");
    }
}


