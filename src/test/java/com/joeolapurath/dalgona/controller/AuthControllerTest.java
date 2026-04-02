package com.joeolapurath.dalgona.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.joeolapurath.dalgona.dto.LoginRequest;
import com.joeolapurath.dalgona.dto.RegisterRequest;
import com.joeolapurath.dalgona.model.Account;
import com.joeolapurath.dalgona.model.Role;
import com.joeolapurath.dalgona.repository.AccountRepository;
import com.joeolapurath.dalgona.security.CustomUserDetailsService;
import com.joeolapurath.dalgona.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthenticationManager authenticationManager;

    @MockitoBean
    private JwtUtil jwtUtil;

    @MockitoBean
    private CustomUserDetailsService customUserDetailsService;

    @MockitoBean
    private AccountRepository accountRepository;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @Test
    @WithMockUser
    void login_shouldReturnJwtToken_whenCredentialsValid() throws Exception {
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("john@example.com");
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(authentication);
        when(jwtUtil.generateToken("john@example.com")).thenReturn("test-jwt-token");
        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(
                Account.builder().email("john@example.com").role(Role.ADMIN).build()
        ));

        LoginRequest request = new LoginRequest("john@example.com", "password123");

        mockMvc.perform(post("/api/auth/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("test-jwt-token"))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    @WithMockUser
    void register_shouldReturnSuccess_whenEmailNotInUse() throws Exception {
        when(accountRepository.findByEmail("new@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("password123")).thenReturn("hashedPassword");
        when(accountRepository.save(any(Account.class))).thenAnswer(i -> i.getArgument(0));

        RegisterRequest request = new RegisterRequest();
        request.setEmail("new@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string("Registered User"));

        verify(accountRepository).save(argThat(account -> account.getRole() == Role.USER));
    }

    @Test
    @WithMockUser
    void register_shouldReturnBadRequest_whenEmailAlreadyInUse() throws Exception {
        Account existingAccount = Account.builder()
                .accountId(1L)
                .email("existing@example.com")
                .build();

        when(accountRepository.findByEmail("existing@example.com")).thenReturn(Optional.of(existingAccount));

        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@example.com");
        request.setPassword("password123");

        mockMvc.perform(post("/api/auth/register")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Email already in use"));

        verify(accountRepository, never()).save(any(Account.class));
    }
}
