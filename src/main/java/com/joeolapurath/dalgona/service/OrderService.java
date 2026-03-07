package com.joeolapurath.dalgona.service;

import com.joeolapurath.dalgona.dto.OrderResponse;
import com.joeolapurath.dalgona.model.Account;
import com.joeolapurath.dalgona.model.Order;
import com.joeolapurath.dalgona.repository.AccountRepository;
import com.joeolapurath.dalgona.repository.OrderRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;

    public OrderService(OrderRepository orderRepository, AccountRepository accountRepository) {
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
    }

    public List<OrderResponse> getOrderHistory(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return orderRepository.findByAccountOrderByRentedAtDesc(account).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getActiveRentals(String email) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return orderRepository.findByAccountAndReturnedAtIsNullOrderByRentedAtDesc(account).stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return toResponse(order);
    }

    private OrderResponse toResponse(Order order) {
        OrderResponse.OrderResponseBuilder builder = OrderResponse.builder()
                .orderId(order.getOrderId())
                .umbrellaId(order.getUmbrella().getUmbrellaId())
                .umbrellaName(order.getUmbrella().getName())
                .pickupStationId(order.getPickupStation().getStationId())
                .pickupStationLocation(order.getPickupStation().getLocation())
                .rentedAt(order.getRentedAt())
                .active(order.getReturnedAt() == null);

        if (order.getReturnStation() != null) {
            builder.returnStationId(order.getReturnStation().getStationId())
                    .returnStationLocation(order.getReturnStation().getLocation());
        }
        if (order.getReturnedAt() != null) {
            builder.returnedAt(order.getReturnedAt());
        }

        return builder.build();
    }
}
