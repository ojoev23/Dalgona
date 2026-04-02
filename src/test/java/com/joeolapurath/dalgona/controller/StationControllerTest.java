package com.joeolapurath.dalgona.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joeolapurath.dalgona.dto.StationResponse;
import com.joeolapurath.dalgona.security.CustomUserDetailsService;
import com.joeolapurath.dalgona.security.JwtUtil;
import com.joeolapurath.dalgona.service.StationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StationController.class)
class StationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private StationService stationService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser
    void getStations_shouldReturnAllStations() throws Exception {
        StationResponse station1 = StationResponse.builder()
                .stationId(1L).location("Downtown").capacity(10).occupied(5).available(5).build();
        StationResponse station2 = StationResponse.builder()
                .stationId(2L).location("Uptown").capacity(8).occupied(2).available(6).build();

        when(stationService.getAllStations()).thenReturn(List.of(station1, station2));

        mockMvc.perform(get("/api/stations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].stationId").value(1))
                .andExpect(jsonPath("$[0].location").value("Downtown"))
                .andExpect(jsonPath("$[1].stationId").value(2))
                .andExpect(jsonPath("$[1].location").value("Uptown"));
    }

    @Test
    @WithMockUser
    void getStation_shouldReturnStation() throws Exception {
        StationResponse station = StationResponse.builder()
                .stationId(1L).location("Downtown").capacity(10).occupied(3).available(7).build();

        when(stationService.getStationById(1L)).thenReturn(station);

        mockMvc.perform(get("/api/stations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationId").value(1))
                .andExpect(jsonPath("$.location").value("Downtown"))
                .andExpect(jsonPath("$.capacity").value(10))
                .andExpect(jsonPath("$.occupied").value(3))
                .andExpect(jsonPath("$.available").value(7));
    }

    @Test
    @WithMockUser(username = "admin@example.com", roles = "ADMIN")
    void createStation_shouldCreateAndReturnStation() throws Exception {
        StationResponse response = StationResponse.builder()
                .stationId(1L).location("Park").capacity(5).occupied(0).available(5).build();

        when(stationService.createStation("Park", 5)).thenReturn(response);

        Map<String, Object> body = Map.of("location", "Park", "capacity", 5);

        mockMvc.perform(post("/api/stations")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stationId").value(1))
                .andExpect(jsonPath("$.location").value("Park"))
                .andExpect(jsonPath("$.capacity").value(5));
    }
}
