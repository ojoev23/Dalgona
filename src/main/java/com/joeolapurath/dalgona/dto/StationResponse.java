package com.joeolapurath.dalgona.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class StationResponse {
    private Long stationId;
    private String location;
    private int capacity;
    private int occupied;
    private int available;
}

