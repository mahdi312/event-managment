package com.event.management.userservice.service;

import com.event.management.userservice.config.JwtService;
import com.event.management.userservice.dto.AuthRequest;
import com.event.management.userservice.dto.JwtResponse;
import com.event.management.userservice.dto.UserRegisterRequest;
import com.event.management.userservice.entity.RoleType;
import com.event.management.userservice.entity.Role;
import com.event.management.userservice.entity.User;
import com.event.management.userservice.exception.UsernameAlreadyExistsException;
import com.event.management.userservice.repository.RoleRepository;
import com.event.management.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Transactional
    public JwtResponse registerUser(UserRegisterRequest request) {
        log.info("Registering user: {}", request.getUsername());
        if (userRepository.existsByUsername(request.getUsername())) {
            log.warn("Username already exists: {}", request.getUsername());
            throw new UsernameAlreadyExistsException("Username is already taken!");
        }

        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Email already in use: {}", request.getEmail());
            throw new UsernameAlreadyExistsException("Email is already in use!");
        }

        // Create new user's account
        User user = User.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .build();

        Set<String> strRoles = request.getRoles();
        Set<Role> roles = new HashSet<>();

        if (strRoles == null || strRoles.isEmpty()) {
            Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
            roles.add(userRole);
        } else {
            strRoles.forEach(role -> {
                switch (role.toLowerCase()) {
                    case "admin":
                        Role adminRole = roleRepository.findByName(RoleType.ROLE_ADMIN)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(adminRole);
                        break;
                    case "event_manager":
                        Role managerRole = roleRepository.findByName(RoleType.ROLE_EVENT_MANAGER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(managerRole);
                        break;
                    default:
                        Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                                .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                        roles.add(userRole);
                }
            });
        }
        user.setRoles(roles);
        userRepository.save(user);
        log.debug("User persisted with roles: {}", roles.stream().map(r -> r.getName().name()).collect(Collectors.toList()));

        return authenticateUser(AuthRequest.builder()
                .username(request.getUsername())
                .password(request.getPassword())
                .build());
    }

    public JwtResponse authenticateUser(AuthRequest request) {
        log.info("Authenticating user: {}", request.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        org.springframework.security.core.userdetails.User springUser = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

        List<String> roles = springUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication: " + request.getUsername()));

        Map<String, Object> claims = new java.util.HashMap<>();
        claims.put("roles", roles);
        claims.put("userId", user.getId());

        String jwt = jwtService.generateToken(claims, user.getUsername());

        log.debug("User authenticated with roles: {}", roles);
        return JwtResponse.builder()
                .token(jwt)
                .id(user.getId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roles(roles)
                .build();
    }
}