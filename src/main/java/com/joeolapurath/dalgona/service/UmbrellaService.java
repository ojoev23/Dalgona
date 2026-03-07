package com.joeolapurath.dalgona.service;

import com.joeolapurath.dalgona.dto.OrderResponse;
import com.joeolapurath.dalgona.model.*;
import com.joeolapurath.dalgona.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class UmbrellaService {

    private final UmbrellaRepository umbrellaRepository;
    private final StationSlotRepository stationSlotRepository;
    private final OrderRepository orderRepository;
    private final AccountRepository accountRepository;
    private final StationRepository stationRepository;

    public UmbrellaService(UmbrellaRepository umbrellaRepository,
                           StationSlotRepository stationSlotRepository,
                           OrderRepository orderRepository,
                           AccountRepository accountRepository,
                           StationRepository stationRepository) {
        this.umbrellaRepository = umbrellaRepository;
        this.stationSlotRepository = stationSlotRepository;
        this.orderRepository = orderRepository;
        this.accountRepository = accountRepository;
        this.stationRepository = stationRepository;
    }

    /**
     * Rent an umbrella from a station.
     * Finds an occupied slot, removes the umbrella, marks it in use, and creates an order.
     */
    @Transactional
    public OrderResponse rentUmbrella(String email, Long stationId) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        // Check if user already has an active rental
        if (orderRepository.existsByAccountAndReturnedAtIsNull(account)) {
            throw new RuntimeException("You already have an active rental. Return it before renting another.");
        }

        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        // Find an occupied slot (one that has an umbrella)
        StationSlot occupiedSlot = station.getSlots().stream()
                .filter(s -> !s.isEmpty())
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No umbrellas available at this station"));

        Umbrella umbrella = occupiedSlot.getUmbrella();

        // Remove umbrella from slot and mark as in use
        occupiedSlot.setUmbrella(null);
        stationSlotRepository.save(occupiedSlot);

        umbrella.setInUse(true);
        umbrellaRepository.save(umbrella);

        // Create order
        Order order = Order.builder()
                .account(account)
                .umbrella(umbrella)
                .pickupStation(station)
                .rentedAt(LocalDateTime.now())
                .build();
        order = orderRepository.save(order);

        return toOrderResponse(order);
    }

    /**
     * Return an umbrella to a station.
     * Finds an empty slot, places the umbrella, marks it not in use, and completes the order.
     */
    @Transactional
    public OrderResponse returnUmbrella(String email, Long stationId, Long umbrellaId) {
        Account account = accountRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        Umbrella umbrella = umbrellaRepository.findById(umbrellaId)
                .orElseThrow(() -> new RuntimeException("Umbrella not found"));

        if (!umbrella.getInUse()) {
            throw new RuntimeException("This umbrella is not currently rented");
        }

        // Find the active order for this umbrella
        Order order = orderRepository.findByUmbrellaAndReturnedAtIsNull(umbrella)
                .orElseThrow(() -> new RuntimeException("No active rental found for this umbrella"));

        // Verify the order belongs to this account
        if (!order.getAccount().getAccountId().equals(account.getAccountId())) {
            throw new RuntimeException("This umbrella is not rented by you");
        }

        Station returnStation = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        // Find an empty slot at the return station
        StationSlot emptySlot = stationSlotRepository
                .findFirstByStationAndUmbrellaIsNullOrderBySlotIndexAsc(returnStation)
                .orElseThrow(() -> new RuntimeException("No empty slots available at this station"));

        // Place umbrella in slot
        emptySlot.setUmbrella(umbrella);
        stationSlotRepository.save(emptySlot);

        umbrella.setInUse(false);
        umbrellaRepository.save(umbrella);

        // Complete the order
        order.setReturnStation(returnStation);
        order.setReturnedAt(LocalDateTime.now());
        orderRepository.save(order);

        return toOrderResponse(order);
    }

    private OrderResponse toOrderResponse(Order order) {
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
