package com.joeolapurath.dalgona.service;

import com.joeolapurath.dalgona.dto.OrderResponse;
import com.joeolapurath.dalgona.model.Account;
import com.joeolapurath.dalgona.model.Order;
import com.joeolapurath.dalgona.model.Station;
import com.joeolapurath.dalgona.model.Umbrella;
import com.joeolapurath.dalgona.repository.AccountRepository;
import com.joeolapurath.dalgona.repository.OrderRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AccountRepository accountRepository;

    @InjectMocks
    private OrderService orderService;

    private Account testAccount;
    private Station pickupStation;
    private Station returnStation;
    private Umbrella testUmbrella;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .accountId(1L)
                .email("john@example.com")
                .build();

        pickupStation = Station.builder()
                .stationId(1L)
                .location("Downtown")
                .capacity(10)
                .build();

        returnStation = Station.builder()
                .stationId(2L)
                .location("Uptown")
                .capacity(10)
                .build();

        testUmbrella = Umbrella.builder()
                .umbrellaId(1L)
                .name("Blue Umbrella")
                .inUse(false)
                .build();
    }

    @Test
    void getOrderHistory_shouldReturnAllOrders() {
        Order activeOrder = Order.builder()
                .orderId(1L)
                .account(testAccount)
                .umbrella(testUmbrella)
                .pickupStation(pickupStation)
                .rentedAt(LocalDateTime.now().minusHours(2))
                .build();

        Order completedOrder = Order.builder()
                .orderId(2L)
                .account(testAccount)
                .umbrella(testUmbrella)
                .pickupStation(pickupStation)
                .returnStation(returnStation)
                .rentedAt(LocalDateTime.now().minusDays(1))
                .returnedAt(LocalDateTime.now().minusDays(1).plusHours(3))
                .build();

        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(orderRepository.findByAccountOrderByRentedAtDesc(testAccount))
                .thenReturn(List.of(activeOrder, completedOrder));

        List<OrderResponse> results = orderService.getOrderHistory("john@example.com");

        assertEquals(2, results.size());

        // First order (active)
        OrderResponse activeResponse = results.get(0);
        assertEquals(1L, activeResponse.getOrderId());
        assertTrue(activeResponse.isActive());
        assertNull(activeResponse.getReturnStationId());

        // Second order (completed)
        OrderResponse completedResponse = results.get(1);
        assertEquals(2L, completedResponse.getOrderId());
        assertFalse(completedResponse.isActive());
        assertEquals(2L, completedResponse.getReturnStationId());
        assertEquals("Uptown", completedResponse.getReturnStationLocation());
    }

    @Test
    void getOrderHistory_shouldReturnEmptyList_whenNoOrders() {
        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(orderRepository.findByAccountOrderByRentedAtDesc(testAccount)).thenReturn(List.of());

        List<OrderResponse> results = orderService.getOrderHistory("john@example.com");

        assertTrue(results.isEmpty());
    }

    @Test
    void getOrderHistory_shouldThrowException_whenAccountNotFound() {
        when(accountRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                orderService.getOrderHistory("missing@example.com"));
    }

    @Test
    void getActiveRentals_shouldReturnOnlyActiveOrders() {
        Order activeOrder = Order.builder()
                .orderId(1L)
                .account(testAccount)
                .umbrella(testUmbrella)
                .pickupStation(pickupStation)
                .rentedAt(LocalDateTime.now().minusHours(2))
                .build();

        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(orderRepository.findByAccountAndReturnedAtIsNullOrderByRentedAtDesc(testAccount))
                .thenReturn(List.of(activeOrder));

        List<OrderResponse> results = orderService.getActiveRentals("john@example.com");

        assertEquals(1, results.size());
        assertTrue(results.get(0).isActive());
        assertNull(results.get(0).getReturnedAt());
    }

    @Test
    void getActiveRentals_shouldThrowException_whenAccountNotFound() {
        when(accountRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                orderService.getActiveRentals("missing@example.com"));
    }

    @Test
    void getOrderById_shouldReturnOrder_whenFound() {
        Order order = Order.builder()
                .orderId(1L)
                .account(testAccount)
                .umbrella(testUmbrella)
                .pickupStation(pickupStation)
                .rentedAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getOrderId());
        assertEquals(1L, response.getUmbrellaId());
        assertEquals("Blue Umbrella", response.getUmbrellaName());
        assertEquals(1L, response.getPickupStationId());
        assertEquals("Downtown", response.getPickupStationLocation());
        assertTrue(response.isActive());
    }

    @Test
    void getOrderById_shouldReturnCompletedOrder() {
        Order order = Order.builder()
                .orderId(1L)
                .account(testAccount)
                .umbrella(testUmbrella)
                .pickupStation(pickupStation)
                .returnStation(returnStation)
                .rentedAt(LocalDateTime.now().minusHours(3))
                .returnedAt(LocalDateTime.now())
                .build();

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        OrderResponse response = orderService.getOrderById(1L);

        assertNotNull(response);
        assertFalse(response.isActive());
        assertNotNull(response.getReturnedAt());
        assertEquals(2L, response.getReturnStationId());
        assertEquals("Uptown", response.getReturnStationLocation());
    }

    @Test
    void getOrderById_shouldThrowException_whenNotFound() {
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                orderService.getOrderById(999L));

        assertEquals("Order not found", exception.getMessage());
    }
}

