package com.joeolapurath.dalgona.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class OrderResponse {
    private Long orderId;
    private Long umbrellaId;
    private Long pickupStationId;
    private String pickupStationLocation;
    private Long returnStationId;
    private String returnStationLocation;
    private LocalDateTime rentedAt;
    private LocalDateTime returnedAt;
    private boolean active;
}

