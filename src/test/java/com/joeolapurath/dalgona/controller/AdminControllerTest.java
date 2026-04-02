package com.joeolapurath.dalgona.controller;

import com.joeolapurath.dalgona.dto.AccountResponse;
import com.joeolapurath.dalgona.security.CustomUserDetailsService;
import com.joeolapurath.dalgona.security.JwtUtil;
import com.joeolapurath.dalgona.service.AdminService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AdminService adminService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(roles = "ADMIN")
    void getAccounts_shouldReturnData_whenAdmin() throws Exception {
        AccountResponse response = AccountResponse.builder()
                .accountId(1L)
                .email("admin@example.com")
                .role("ADMIN")
                .build();

        when(adminService.getAllAccounts()).thenReturn(List.of(response));

        mockMvc.perform(get("/api/admin/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].accountId").value(1))
                .andExpect(jsonPath("$[0].role").value("ADMIN"));
    }

    @Test
    void getAccounts_shouldReturnUnauthorized_whenAnonymous() throws Exception {
        mockMvc.perform(get("/api/admin/accounts"))
                .andExpect(status().isUnauthorized());
    }
}
