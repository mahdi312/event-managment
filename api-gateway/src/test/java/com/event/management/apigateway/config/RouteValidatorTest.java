package com.event.management.apigateway.config;

import org.junit.jupiter.api.Test;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;

import static org.assertj.core.api.Assertions.assertThat;

class RouteValidatorTest {

    @Test
    void openEndpointsShouldNotBeSecured() {
        RouteValidator validator = new RouteValidator();
        ServerHttpRequest request = MockServerHttpRequest.get("/api/v1/auth/login").build();
        assertThat(validator.isSecured.test(request)).isFalse();
    }

    @Test
    void securedEndpointsShouldBeSecured() {
        RouteValidator validator = new RouteValidator();
        ServerHttpRequest request = MockServerHttpRequest.get("/api/v1/events").build();
        assertThat(validator.isSecured.test(request)).isTrue();
    }
}


