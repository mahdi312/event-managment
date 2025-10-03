package com.event.management.userservice.service;

import com.event.management.userservice.config.JwtService;
import com.event.management.userservice.dto.AuthRequest;
import com.event.management.userservice.dto.JwtResponse;
import com.event.management.userservice.dto.UserRegisterRequest;
import com.event.management.userservice.entity.Role;
import com.event.management.userservice.entity.RoleType;
import com.event.management.userservice.entity.User;
import com.event.management.userservice.repository.RoleRepository;
import com.event.management.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void authenticateUser_success() {
        AuthRequest request = AuthRequest.builder().username("Mahdi").password("password").build();

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        "Mahdi", "password", List.of(new SimpleGrantedAuthority("ROLE_USER"))
                ),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(anyMap(), anyString())).thenReturn("jwt");
        when(userRepository.findByUsername("Mahdi")).thenReturn(Optional.of(User.builder().id(1L).username("Mahdi").email("j@e.com").build()));

        JwtResponse response = authService.authenticateUser(request);

        assertThat(response.getToken()).isEqualTo("jwt");
        assertThat(response.getUsername()).isEqualTo("Mahdi");
    }

    @Test
    void registerUser_assignsDefaultRole() {
        UserRegisterRequest req = UserRegisterRequest.builder()
                .username("Mahdi").email("mahdi@example.com").password("password").build();

        when(userRepository.existsByUsername("Mahdi")).thenReturn(false);
        when(userRepository.existsByEmail("mahdi@example.com")).thenReturn(false);
        when(passwordEncoder.encode("password")).thenReturn("ENC");
        when(roleRepository.findByName(RoleType.ROLE_USER)).thenReturn(Optional.of(Role.builder().id(1L).name(RoleType.ROLE_USER).build()));
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        Authentication authentication = new UsernamePasswordAuthenticationToken(
                new org.springframework.security.core.userdetails.User(
                        "Mahdi", "ENC", List.of(new SimpleGrantedAuthority("ROLE_USER"))
                ),
                null,
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );
        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(jwtService.generateToken(anyMap(), anyString())).thenReturn("jwt2");
        when(userRepository.findByUsername("Mahdi")).thenReturn(Optional.of(User.builder().id(5L).username("Mahdi").email("mahdi@example.com").build()));

        JwtResponse response = authService.registerUser(req);
        assertThat(response.getToken()).isEqualTo("jwt2");
        verify(userRepository).save(any(User.class));
    }
}


