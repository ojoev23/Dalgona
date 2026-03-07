package com.joeolapurath.dalgona.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AccountResponse {
    private Long accountId;
    private String name;
    private String email;
    private String address;
    private String phone;
    private int age;
}

