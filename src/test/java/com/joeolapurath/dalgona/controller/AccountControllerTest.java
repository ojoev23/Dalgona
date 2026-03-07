package com.joeolapurath.dalgona.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joeolapurath.dalgona.dto.AccountResponse;
import com.joeolapurath.dalgona.dto.AccountUpdateRequest;
import com.joeolapurath.dalgona.security.CustomUserDetailsService;
import com.joeolapurath.dalgona.security.JwtUtil;
import com.joeolapurath.dalgona.service.AccountService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AccountService accountService;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @Test
    @WithMockUser(username = "john@example.com")
    void getProfile_shouldReturnAccountResponse() throws Exception {
        AccountResponse response = AccountResponse.builder()
                .accountId(1L)
                .name("John Doe")
                .email("john@example.com")
                .address("123 Main St")
                .phone("555-1234")
                .age(25)
                .build();

        when(accountService.getProfile("john@example.com")).thenReturn(response);

        mockMvc.perform(get("/api/account/me"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accountId").value(1))
                .andExpect(jsonPath("$.name").value("John Doe"))
                .andExpect(jsonPath("$.email").value("john@example.com"))
                .andExpect(jsonPath("$.address").value("123 Main St"))
                .andExpect(jsonPath("$.phone").value("555-1234"))
                .andExpect(jsonPath("$.age").value(25));
    }

    @Test
    @WithMockUser(username = "john@example.com")
    void updateProfile_shouldReturnUpdatedAccountResponse() throws Exception {
        AccountResponse response = AccountResponse.builder()
                .accountId(1L)
                .name("Jane Doe")
                .email("john@example.com")
                .address("456 Oak Ave")
                .phone("555-5678")
                .age(30)
                .build();

        when(accountService.updateProfile(eq("john@example.com"), any(AccountUpdateRequest.class)))
                .thenReturn(response);

        AccountUpdateRequest request = new AccountUpdateRequest();
        request.setName("Jane Doe");
        request.setAddress("456 Oak Ave");
        request.setPhone("555-5678");
        request.setAge(30);

        mockMvc.perform(put("/api/account/me")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Jane Doe"))
                .andExpect(jsonPath("$.address").value("456 Oak Ave"))
                .andExpect(jsonPath("$.phone").value("555-5678"))
                .andExpect(jsonPath("$.age").value(30));
    }
}
