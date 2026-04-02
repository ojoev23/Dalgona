package com.joeolapurath.dalgona.controller;

import com.joeolapurath.dalgona.dto.OrderResponse;
import com.joeolapurath.dalgona.security.CustomUserDetailsService;
import com.joeolapurath.dalgona.security.JwtUtil;
import com.joeolapurath.dalgona.service.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(username = "john@example.com")
    void getOrderHistory_shouldReturnOrders() throws Exception {
        LocalDateTime now = LocalDateTime.of(2026, 3, 7, 12, 0);
        OrderResponse order = OrderResponse.builder()
                .orderId(1L)
                .umbrellaId(1L)
                .pickupStationId(1L)
                .pickupStationLocation("Downtown")
                .rentedAt(now)
                .active(true)
                .build();

        when(orderService.getOrderHistory("john@example.com")).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].umbrellaId").value(1))
                .andExpect(jsonPath("$[0].pickupStationId").value(1))
                .andExpect(jsonPath("$[0].pickupStationLocation").value("Downtown"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void getOrderHistory_shouldReturnEmptyList() throws Exception {
        when(orderService.getOrderHistory("john@example.com")).thenReturn(List.of());

        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void getActiveRentals_shouldReturnActiveOrders() throws Exception {
        LocalDateTime now = LocalDateTime.of(2026, 3, 7, 12, 0);
        OrderResponse order = OrderResponse.builder()
                .orderId(1L)
                .umbrellaId(1L)
                .pickupStationId(1L)
                .pickupStationLocation("Downtown")
                .rentedAt(now)
                .active(true)
                .build();

        when(orderService.getActiveRentals("john@example.com")).thenReturn(List.of(order));

        mockMvc.perform(get("/api/orders/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].orderId").value(1))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void getOrderById_shouldReturnOrder() throws Exception {
        LocalDateTime now = LocalDateTime.of(2026, 3, 7, 12, 0);
        OrderResponse order = OrderResponse.builder()
                .orderId(1L)
                .umbrellaId(1L)
                .pickupStationId(1L)
                .pickupStationLocation("Downtown")
                .returnStationId(2L)
                .returnStationLocation("Uptown")
                .rentedAt(now.minusHours(3))
                .returnedAt(now)
                .active(false)
                .build();

        when(orderService.getOrderById(1L)).thenReturn(order);

        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.orderId").value(1))
                .andExpect(jsonPath("$.active").value(false))
                .andExpect(jsonPath("$.returnStationId").value(2))
                .andExpect(jsonPath("$.returnStationLocation").value("Uptown"));
    }
}
