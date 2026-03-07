package com.joeolapurath.dalgona.security;

import com.joeolapurath.dalgona.model.Account;
import com.joeolapurath.dalgona.repository.AccountRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private CustomUserDetailsService customUserDetailsService;

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenAccountExists() {
        Account account = Account.builder()
                .accountId(1L)
                .email("john@example.com")
                .passwordHash("hashedPassword123")
                .build();

        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(account));

        UserDetails userDetails = customUserDetailsService.loadUserByUsername("john@example.com");

        assertNotNull(userDetails);
        assertEquals("john@example.com", userDetails.getUsername());
        assertEquals("hashedPassword123", userDetails.getPassword());
        assertTrue(userDetails.getAuthorities().isEmpty());
    }

    @Test
    void loadUserByUsername_shouldThrowException_whenAccountNotFound() {
        when(accountRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () ->
                customUserDetailsService.loadUserByUsername("missing@example.com"));

        assertEquals("User not found with email: missing@example.com", exception.getMessage());
    }
}

