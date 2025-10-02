package com.event.management.apigateway.config;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthFilter implements GatewayFilter {

    private final JwtService jwtService;
    private final RouteValidator validator;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();

        if (validator.isSecured.test(request)) {
            if (!request.getHeaders().containsKey("Authorization")) {
                log.warn("Missing Authorization header for secured endpoint: {}", request.getURI().getPath());
                return this.onError(exchange, "Missing Authorization header", HttpStatus.UNAUTHORIZED);
            }

            String authHeader = request.getHeaders().getOrEmpty("Authorization").get(0);
            if (authHeader != null && authHeader.startsWith("Bearer ")) {
                String token = authHeader.substring(7);
                try {
                    jwtService.validateToken(token);
                    addJwtClaimsToRequest(request, token);
                } catch (ExpiredJwtException e) {
                    log.warn("Expired JWT token for {}: {}", request.getURI().getPath(), e.getMessage());
                    return this.onError(exchange, "JWT token is expired", HttpStatus.UNAUTHORIZED);
                } catch (MalformedJwtException | SignatureException | UnsupportedJwtException | IllegalArgumentException e) {
                    log.warn("Invalid JWT token for {}: {}", request.getURI().getPath(), e.getMessage());
                    return this.onError(exchange, "Invalid JWT token", HttpStatus.UNAUTHORIZED);
                }
            } else {
                log.warn("Invalid Authorization header format for secured endpoint: {}", request.getURI().getPath());
                return this.onError(exchange, "Invalid Authorization header format", HttpStatus.UNAUTHORIZED);
            }
        }
        return chain.filter(exchange);
    }

    private Mono<Void> onError(ServerWebExchange exchange, String err, HttpStatus httpStatus) {
        ServerHttpResponse response = exchange.getResponse();
        response.setStatusCode(httpStatus);
        return response.setComplete();
    }

    private void addJwtClaimsToRequest(ServerHttpRequest request, String token) {
        String username = jwtService.extractUsername(token);
        List<String> roles = jwtService.extractRoles(token);
        Long userId = jwtService.extractUserId(token);

        request.mutate()
                .header("X-Auth-Username", username)
                .header("X-Auth-Roles", String.join(",", roles))
                .header("X-Auth-UserId", String.valueOf(userId))
                .build();
    }
}