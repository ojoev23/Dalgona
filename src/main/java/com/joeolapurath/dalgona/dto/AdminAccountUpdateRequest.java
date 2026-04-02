package com.joeolapurath.dalgona.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminAccountUpdateRequest {
    private String name;
    private String address;
    private String phone;
    private Integer age;
    private String role;
}
