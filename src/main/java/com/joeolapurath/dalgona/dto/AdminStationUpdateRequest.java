package com.joeolapurath.dalgona.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminStationUpdateRequest {
    private String location;
    private Integer capacity;
}
