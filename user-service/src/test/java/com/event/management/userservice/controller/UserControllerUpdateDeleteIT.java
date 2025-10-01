package com.event.management.userservice.controller;

import com.event.management.userservice.dto.AuthRequest;
import com.event.management.userservice.dto.UserRegisterRequest;
import com.event.management.userservice.dto.UserUpdateRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerUpdateDeleteIT {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    private String adminToken;
    private Long user2Id;
    private String adminUsername;
    private String adminEmail;
    private String user2Username;
    private String user2Email;

    @BeforeEach
    void setUp() throws Exception {
        String suffix = String.format("%06d", (System.nanoTime() % 1_000_000));
        adminUsername = "MahdiMst_" + suffix;
        adminEmail = "mahdimst_" + suffix + "@initial.com";
        user2Username = "MahdiUser_" + suffix;
        user2Email = "mahdiuser_" + suffix + "@example.com";

        UserRegisterRequest adminUser = UserRegisterRequest.builder()
                .username(adminUsername)
                .email(adminEmail)
                .password("secret1")
                .roles(java.util.Set.of("admin"))
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(adminUser)))
                .andExpect(status().isCreated());

        UserRegisterRequest user2 = UserRegisterRequest
                .builder()
                .username(user2Username)
                .email(user2Email)
                .password("secret2")
                .build();

        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(user2)))
                .andExpect(status().isCreated());

        String adminResponse = mockMvc.perform(
                        post("/api/v1/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(
                                         AuthRequest
                                                .builder()
                                                 .username(adminUsername)
                                                .password("secret1")
                                                .build())
                                ))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(adminResponse);
        adminToken = node.get("token").asText();
        assertThat(adminToken).isNotBlank();

        String user2Resp = mockMvc
                .perform(get("/api/v1/users/username/{username}", user2Username)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        JsonNode userNode = objectMapper.readTree(user2Resp);
        user2Id = userNode.get("id").asLong();
    }

    @Test
    void updateUser_by_admin_success() throws Exception {
        UserUpdateRequest update = UserUpdateRequest.builder()
                .email("mahdi@updated.com")
                .build();

        mockMvc.perform(put("/api/v1/users/{id}", user2Id)
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk());
    }

    @Test
    void deleteUser_by_admin_success() throws Exception {
        mockMvc.perform(delete("/api/v1/users/{id}", user2Id)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isNoContent());
    }

    @Test
    void update_or_delete_as_non_admin_should_forbid() throws Exception {
        String normalSuffix = String.format("%06d", (System.nanoTime() % 1_000_000));
        String normalUsername = "Mahdi123_" + normalSuffix; // stays within 20 chars
        String normalEmail = "mahdi123_" + normalSuffix + "@example.com";
        UserRegisterRequest newNormalUser = UserRegisterRequest.builder()
                .username(normalUsername).email(normalEmail).password("secret3").build();
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newNormalUser)))
                .andExpect(status().isCreated());

        String newNormalUserLogin = mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                         .content(objectMapper.writeValueAsString(
                                 AuthRequest.builder().username(normalUsername).password("secret3").build())))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();

        JsonNode newNormalUserNode = objectMapper.readTree(newNormalUserLogin);
        String newNormalUserToken = newNormalUserNode.get("token").asText();

        UserUpdateRequest update = UserUpdateRequest.builder().email("blocked@example.com").build();
        mockMvc.perform(put("/api/v1/users/{id}", user2Id)
                        .header("Authorization", "Bearer " + newNormalUserToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/v1/users/{id}", user2Id)
                        .header("Authorization", "Bearer " + newNormalUserToken))
                .andExpect(status().isForbidden());
    }
}


