package com.joeolapurath.dalgona.service;

import com.joeolapurath.dalgona.dto.AccountResponse;
import com.joeolapurath.dalgona.dto.AccountUpdateRequest;
import com.joeolapurath.dalgona.model.Account;
import com.joeolapurath.dalgona.repository.AccountRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountServiceTest {

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private AccountService accountService;

    private Account testAccount;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .accountId(1L)
                .name("John Doe")
                .email("john@example.com")
                .address("123 Main St")
                .phone("555-1234")
                .age(25)
                .passwordHash("hashedPassword")
                .build();
    }

    @Test
    void getAccountByEmail_shouldReturnAccount_whenAccountExists() {
        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));

        Account result = accountService.getAccountByEmail("john@example.com");

        assertNotNull(result);
        assertEquals("john@example.com", result.getEmail());
        assertEquals("John Doe", result.getName());
        verify(accountRepository).findByEmail("john@example.com");
    }

    @Test
    void getAccountByEmail_shouldThrowException_whenAccountNotFound() {
        when(accountRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                accountService.getAccountByEmail("missing@example.com"));

        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    void getProfile_shouldReturnAccountResponse() {
        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));

        AccountResponse response = accountService.getProfile("john@example.com");

        assertNotNull(response);
        assertEquals(1L, response.getAccountId());
        assertEquals("John Doe", response.getName());
        assertEquals("john@example.com", response.getEmail());
        assertEquals("123 Main St", response.getAddress());
        assertEquals("555-1234", response.getPhone());
        assertEquals(25, response.getAge());
    }

    @Test
    void getProfile_shouldThrowException_whenAccountNotFound() {
        when(accountRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                accountService.getProfile("missing@example.com"));
    }

    @Test
    void updateProfile_shouldUpdateAllFields() {
        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        AccountUpdateRequest request = new AccountUpdateRequest();
        request.setName("Jane Doe");
        request.setAddress("456 Oak Ave");
        request.setPhone("555-5678");
        request.setAge(30);

        AccountResponse response = accountService.updateProfile("john@example.com", request);

        assertNotNull(response);
        verify(accountRepository).save(testAccount);
        assertEquals("Jane Doe", testAccount.getName());
        assertEquals("456 Oak Ave", testAccount.getAddress());
        assertEquals("555-5678", testAccount.getPhone());
        assertEquals(30, testAccount.getAge());
    }

    @Test
    void updateProfile_shouldUpdateOnlyProvidedFields() {
        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        AccountUpdateRequest request = new AccountUpdateRequest();
        request.setName("Jane Doe");
        // address, phone, age are null/0

        AccountResponse response = accountService.updateProfile("john@example.com", request);

        assertNotNull(response);
        assertEquals("Jane Doe", testAccount.getName());
        assertEquals("123 Main St", testAccount.getAddress()); // unchanged
        assertEquals("555-1234", testAccount.getPhone()); // unchanged
        assertEquals(25, testAccount.getAge()); // unchanged because age == 0 in request
    }

    @Test
    void updateProfile_shouldNotUpdateAge_whenZero() {
        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(accountRepository.save(any(Account.class))).thenReturn(testAccount);

        AccountUpdateRequest request = new AccountUpdateRequest();
        // age defaults to 0

        accountService.updateProfile("john@example.com", request);

        assertEquals(25, testAccount.getAge()); // should remain unchanged
    }

    @Test
    void updateProfile_shouldThrowException_whenAccountNotFound() {
        when(accountRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        AccountUpdateRequest request = new AccountUpdateRequest();
        request.setName("Test");

        assertThrows(RuntimeException.class, () ->
                accountService.updateProfile("missing@example.com", request));
    }
}

