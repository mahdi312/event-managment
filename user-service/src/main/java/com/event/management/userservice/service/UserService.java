package com.event.management.userservice.service;

import com.event.management.userservice.dto.UserResponse;
import com.event.management.userservice.dto.UserUpdateRequest;
import com.event.management.userservice.entity.Role;
import com.event.management.userservice.entity.RoleType;
import com.event.management.userservice.entity.User;
import com.event.management.userservice.exception.UserNotFoundException;
import com.event.management.userservice.exception.UsernameAlreadyExistsException;
import com.event.management.userservice.mapper.UserMapper;
import com.event.management.userservice.repository.RoleRepository;
import com.event.management.userservice.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;


    @Cacheable(value = "users", key = "#id")
    @Transactional(readOnly = true)
    public UserResponse getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        return userMapper.toUserResponse(user);
    }

    @Cacheable(value = "users", key = "#username")
    @Transactional(readOnly = true)
    public UserResponse getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException("User not found with username: " + username));
        return userMapper.toUserResponse(user);
    }

    @Transactional(readOnly = true)
    public List<UserResponse> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toUserResponseList(users);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @CachePut(value = "users", key = "#id")
    @CacheEvict(value = "users", key = "#result.username")
    @Transactional
    public UserResponse updateUser(Long id, UserUpdateRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));


        if (request.getUsername() != null && !request.getUsername().equals(existingUser.getUsername())) {
            if (userRepository.existsByUsername(request.getUsername())) {
                throw new UsernameAlreadyExistsException("Username '" + request.getUsername() + "' is already taken.");
            }
        }


        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new UsernameAlreadyExistsException("Email '" + request.getEmail() + "' is already in use.");
            }
        }

        userMapper.updateUserFromDto(request, existingUser);

        if (request.getRoles() != null) {
            if (!request.getRoles().isEmpty()) {
                Set<Role> newRoles = new HashSet<>();
                for (String roleName : request.getRoles()) {
                    RoleType roleType;
                    try {
                        roleType = RoleType.valueOf("ROLE_" + roleName.toUpperCase());
                    } catch (IllegalArgumentException e) {
                        throw new IllegalArgumentException("Invalid role specified: " + roleName);
                    }
                    Role role = roleRepository.findByName(roleType)
                            .orElseThrow(() -> new RuntimeException("Error: Role " + roleType + " is not found."));
                    newRoles.add(role);
                }
                existingUser.setRoles(newRoles);
            } else {
                Role userRole = roleRepository.findByName(RoleType.ROLE_USER)
                        .orElseThrow(() -> new RuntimeException("Error: Role is not found."));
                existingUser.setRoles(Set.of(userRole));
            }
        }

        User updatedUser = userRepository.save(existingUser);
        return userMapper.toUserResponse(updatedUser);
    }


    @PreAuthorize("hasRole('ADMIN')")
    @Caching(evict = {
            @CacheEvict(value = "users", key = "#id"),
            @CacheEvict(value = "users", key = "#result.username")
    })
    @Transactional
    public void deleteUser(Long id) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found with id: " + id));
        userRepository.delete(existingUser);
    }
}