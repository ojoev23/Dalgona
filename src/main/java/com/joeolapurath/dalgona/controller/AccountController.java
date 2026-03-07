package com.joeolapurath.dalgona.controller;

import com.joeolapurath.dalgona.dto.AccountResponse;
import com.joeolapurath.dalgona.dto.AccountUpdateRequest;
import com.joeolapurath.dalgona.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping("/me")
    public ResponseEntity<AccountResponse> getProfile(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(accountService.getProfile(email));
    }

    @PutMapping("/me")
    public ResponseEntity<AccountResponse> updateProfile(@RequestBody AccountUpdateRequest request,
                                                          Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(accountService.updateProfile(email, request));
    }
}
