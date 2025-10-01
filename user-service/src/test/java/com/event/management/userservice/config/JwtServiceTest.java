package com.event.management.userservice.config;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();
        ReflectionTestUtils.setField(jwtService, "secretKey", "404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970");
        ReflectionTestUtils.setField(jwtService, "jwtExpiration", 3600000L); // 1 hour
    }

    @Test
    void generateAndValidateToken_roundTrip() {
        TestingAuthenticationToken auth = new TestingAuthenticationToken("Mahdi", "password", "ROLE_USER");
        String token = jwtService.generateToken(auth);

        String username = jwtService.extractUsername(token);
        assertThat(username).isEqualTo("Mahdi");
    }
}


