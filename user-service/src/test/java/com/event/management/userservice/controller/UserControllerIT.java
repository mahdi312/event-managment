package com.event.management.userservice.controller;

import com.event.management.userservice.dto.AuthRequest;
import com.event.management.userservice.dto.UserRegisterRequest;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private String token;

    @BeforeEach
    void setUp() throws Exception {
        UserRegisterRequest register = UserRegisterRequest.builder()
                .username("Mahdi").email("mahdi@example.com").password("secret1").build();
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(register)))
            .andExpect(status().isCreated());

        String loginResponse = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(AuthRequest.builder().username("Mahdi").password("secret1").build())))
            .andExpect(status().isOk())
            .andReturn().getResponse().getContentAsString();

        JsonNode node = objectMapper.readTree(loginResponse);
        token = node.get("token").asText();
        assertThat(token).isNotBlank();
    }

    @Test
    void getUserByUsername_requiresAuth_thenReturns() throws Exception {
        mockMvc.perform(get("/api/v1/users/username/{username}", "Mahdi")
                .header("Authorization", "Bearer " + token))
            .andExpect(status().isOk());
    }
}


