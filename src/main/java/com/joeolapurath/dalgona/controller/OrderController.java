package com.joeolapurath.dalgona.controller;

import com.joeolapurath.dalgona.dto.OrderResponse;
import com.joeolapurath.dalgona.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public ResponseEntity<List<OrderResponse>> getOrderHistory(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.getOrderHistory(email));
    }

    @GetMapping("/active")
    public ResponseEntity<List<OrderResponse>> getActiveRentals(Authentication authentication) {
        String email = authentication.getName();
        return ResponseEntity.ok(orderService.getActiveRentals(email));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderResponse> getOrder(@PathVariable Long id) {
        return ResponseEntity.ok(orderService.getOrderById(id));
    }
}
