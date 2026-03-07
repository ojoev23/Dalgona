package com.joeolapurath.dalgona.controller;

import com.joeolapurath.dalgona.dto.OrderResponse;
import com.joeolapurath.dalgona.dto.RentRequest;
import com.joeolapurath.dalgona.dto.ReturnRequest;
import com.joeolapurath.dalgona.service.UmbrellaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/umbrellas")
public class UmbrellaController {

    private final UmbrellaService umbrellaService;

    public UmbrellaController(UmbrellaService umbrellaService) {
        this.umbrellaService = umbrellaService;
    }

    @PostMapping("/rent")
    public ResponseEntity<OrderResponse> rentUmbrella(@RequestBody RentRequest request,
                                                       Authentication authentication) {
        String email = authentication.getName();
        OrderResponse response = umbrellaService.rentUmbrella(email, request.getStationId());
        return ResponseEntity.ok(response);
    }

    @PostMapping("/return")
    public ResponseEntity<OrderResponse> returnUmbrella(@RequestBody ReturnRequest request,
                                                         Authentication authentication) {
        String email = authentication.getName();
        OrderResponse response = umbrellaService.returnUmbrella(email, request.getStationId(), request.getUmbrellaId());
        return ResponseEntity.ok(response);
    }
}
