package com.joeolapurath.dalgona.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joeolapurath.dalgona.dto.OrderResponse;
import com.joeolapurath.dalgona.dto.RentRequest;
import com.joeolapurath.dalgona.dto.ReturnRequest;
import com.joeolapurath.dalgona.security.CustomUserDetailsService;
import com.joeolapurath.dalgona.security.JwtUtil;
import com.joeolapurath.dalgona.service.UmbrellaService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UmbrellaController.class)
class UmbrellaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UmbrellaService umbrellaService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(username = "john@example.com")
    void rentUmbrella_shouldReturnOrderResponse() throws Exception {
        LocalDateTime now = LocalDateTime.of(2026, 3, 7, 12, 0);
        OrderResponse response = OrderResponse.builder()
                .orderId(1L)
                .umbrellaId(1L)
                .pickupStationId(1L)
                .pickupStationLocation("Downtown")
                .rentedAt(now)
                .active(true)
                .build();

        when(umbrellaService.rentUmbrella(eq("john@example.com"), eq(1L))).thenReturn(response);

        RentRequest request = new RentRequest();
        request.setStationId(1L);

        mockMvc.perform(post("/api/umbrellas/rent")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.umbrellaId").value(1))
                .andExpect(jsonPath("$.pickupStationId").value(1))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void returnUmbrella_shouldReturnCompletedOrder() throws Exception {
        LocalDateTime rentedAt = LocalDateTime.of(2026, 3, 7, 10, 0);
        LocalDateTime returnedAt = LocalDateTime.of(2026, 3, 7, 14, 0);
        OrderResponse response = OrderResponse.builder()
                .orderId(1L)
                .umbrellaId(1L)
                .pickupStationId(1L)
                .pickupStationLocation("Downtown")
                .returnStationId(2L)
                .returnStationLocation("Uptown")
                .rentedAt(rentedAt)
                .returnedAt(returnedAt)
                .active(false)
                .build();

        when(umbrellaService.returnUmbrella(eq("john@example.com"), eq(2L), eq(1L))).thenReturn(response);

        ReturnRequest request = new ReturnRequest();
        request.setStationId(2L);
        request.setUmbrellaId(1L);

        mockMvc.perform(post("/api/umbrellas/return")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.returnStationId").value(2))
                .andExpect(jsonPath("$.returnStationLocation").value("Uptown"));
    }
}
