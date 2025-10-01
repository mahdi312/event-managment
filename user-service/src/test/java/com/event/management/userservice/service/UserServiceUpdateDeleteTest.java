package com.event.management.userservice.service;

import com.event.management.userservice.dto.UserResponse;
import com.event.management.userservice.dto.UserUpdateRequest;
import com.event.management.userservice.entity.Role;
import com.event.management.userservice.entity.RoleType;
import com.event.management.userservice.entity.User;
import com.event.management.userservice.exception.UserNotFoundException;
import com.event.management.userservice.mapper.UserMapper;
import com.event.management.userservice.repository.RoleRepository;
import com.event.management.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UserServiceUpdateDeleteTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private User existing;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        existing = User.builder().id(10L).username("Mahdi").email("mahdi@old.com").build();
    }

    @Test
    void updateUser_updatesFields_andRoles() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(userRepository.existsByUsername("Mahdi"))
                .thenReturn(false);
        when(userRepository.existsByEmail("mahdi@new.com"))
                .thenReturn(false);

        UserUpdateRequest req = UserUpdateRequest.builder()
                .username("Mahdi")
                .email("mahdi@new.com")
                .roles(Set.of("admin"))
                .build();

        when(roleRepository.findByName(RoleType.ROLE_ADMIN))
                .thenReturn(Optional.of(Role.builder().id(2L).name(RoleType.ROLE_ADMIN).build()));

        doAnswer(inv -> {
            UserUpdateRequest r = inv.getArgument(0);
            User u = inv.getArgument(1);
            if (r.getUsername() != null) u.setUsername(r.getUsername());
            if (r.getEmail() != null) u.setEmail(r.getEmail());
            return null;
        }).when(userMapper).updateUserFromDto(any(UserUpdateRequest.class), any(User.class));

        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));
        when(userMapper.toUserResponse(any(User.class)))
                .thenAnswer(inv -> {
                    User u = inv.getArgument(0);
                    return UserResponse.builder().id(u.getId()).username(u.getUsername()).email(u.getEmail()).build();
                });

        UserResponse resp = userService.updateUser(10L, req);

        assertThat(resp.getEmail()).isEqualTo("mahdi@new.com");
        ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(captor.capture());
        assertThat(captor.getValue().getRoles()).extracting(r -> r.getName()).contains(RoleType.ROLE_ADMIN);
    }

    @Test
    void deleteUser_removesEntity() {
        when(userRepository.findById(10L)).thenReturn(Optional.of(existing));
        userService.deleteUser(10L);
        verify(userRepository).delete(existing);
    }

    @Test
    void updateUser_notFound_throws() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.updateUser(99L, UserUpdateRequest.builder().build()))
                .isInstanceOf(UserNotFoundException.class);
    }
}


