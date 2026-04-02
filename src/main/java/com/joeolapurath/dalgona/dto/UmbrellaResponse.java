package com.joeolapurath.dalgona.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UmbrellaResponse {
    private Long umbrellaId;
    private boolean inUse;
}
