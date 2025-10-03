package com.event.management.ticketingservice.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class BookingControllerIT {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getMyBookingsShouldRequireAuth() throws Exception {
        mockMvc.perform(get("/api/v1/bookings/my-bookings"))
                .andExpect(status().is4xxClientError());
    }
}


