package com.event.management.apigateway.config;

import lombok.RequiredArgsConstructor;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class GatewayConfig {

    private final JwtAuthFilter authFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("user-service", r -> r.path("/api/v1/auth/**", "/api/v1/users/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://user-service")
                )
                .route("event-service", r -> r.path("/api/v1/events/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://event-service")
                )
                .route("ticketing-service", r -> r.path("/api/v1/bookings/**")
                        .filters(f -> f.filter(authFilter))
                        .uri("lb://ticketing-service")
                )
                .route("fallback-route", r -> r.path("/fallback")
                        .uri("forward:/fallback")
                )
                .build();
    }
}