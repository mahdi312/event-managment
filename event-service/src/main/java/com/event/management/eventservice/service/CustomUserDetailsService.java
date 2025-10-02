package com.event.management.eventservice.service;

import com.event.management.eventservice.config.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final JwtService jwtService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
      //todo ensure jwtAuthFilter correctly populate securityContext
        throw new UsernameNotFoundException("User details cannot be loaded from local DB in this service. Rely on JWT.");
    }

    public UserDetails buildUserDetailsFromJwt(String jwt) {
        String username = jwtService.extractUsername(jwt);
        List<String> roles = jwtService.extractRoles(jwt);
        Long userId = jwtService.extractUserId(jwt);

        if (username == null || roles == null) {
            throw new IllegalArgumentException("Invalid JWT: missing username or roles");
        }

        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());

        return new User(username, "", authorities);
    }
}