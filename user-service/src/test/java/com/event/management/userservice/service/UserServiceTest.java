package com.event.management.userservice.service;

import com.event.management.userservice.dto.UserResponse;
import com.event.management.userservice.entity.User;
import com.event.management.userservice.exception.UserNotFoundException;
import com.event.management.userservice.mapper.UserMapper;
import com.event.management.userservice.repository.RoleRepository;
import com.event.management.userservice.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private RoleRepository roleRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getUserById_returnsMappedResponse() {
        User user = User.builder().id(1L).username("Mahdi").email("mahdi@example.com").build();
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toUserResponse(user)).thenReturn(UserResponse.builder().id(1L).username("Mahdi").email("mahdi@example.com").build());

        UserResponse resp = userService.getUserById(1L);
        assertThat(resp.getId()).isEqualTo(1L);
    }

    @Test
    void getAllUsers_returnsList() {
        when(userRepository.findAll()).thenReturn(List.of());
        when(userMapper.toUserResponseList(any())).thenReturn(List.of());
        List<UserResponse> resp = userService.getAllUsers();
        assertThat(resp).isNotNull();
    }

    @Test
    void getUserById_notFound_throws() {
        when(userRepository.findById(2L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> userService.getUserById(2L)).isInstanceOf(UserNotFoundException.class);
    }
}


