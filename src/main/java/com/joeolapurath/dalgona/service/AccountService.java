package com.joeolapurath.dalgona.service;

import com.joeolapurath.dalgona.dto.AccountResponse;
import com.joeolapurath.dalgona.dto.AccountUpdateRequest;
import com.joeolapurath.dalgona.model.Account;
import com.joeolapurath.dalgona.repository.AccountRepository;
import org.springframework.stereotype.Service;

@Service
public class AccountService {

    private final AccountRepository accountRepository;

    public AccountService(AccountRepository accountRepository) {
        this.accountRepository = accountRepository;
    }

    public Account getAccountByEmail(String email) {
        return accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }

    public AccountResponse getProfile(String email) {
        Account account = getAccountByEmail(email);
        return toResponse(account);
    }

    public AccountResponse updateProfile(String email, AccountUpdateRequest request) {
        Account account = getAccountByEmail(email);
        if (request.getName() != null) account.setName(request.getName());
        if (request.getAddress() != null) account.setAddress(request.getAddress());
        if (request.getPhone() != null) account.setPhone(request.getPhone());
        if (request.getAge() > 0) account.setAge(request.getAge());
        accountRepository.save(account);
        return toResponse(account);
    }

    private AccountResponse toResponse(Account account) {
        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .name(account.getName())
                .email(account.getEmail())
                .address(account.getAddress())
                .phone(account.getPhone())
                .age(account.getAge())
                .build();
    }
}
