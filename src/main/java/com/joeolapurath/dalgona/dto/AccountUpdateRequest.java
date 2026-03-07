package com.joeolapurath.dalgona.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AccountUpdateRequest {
    private String name;
    private String address;
    private String phone;
    private int age;
}

