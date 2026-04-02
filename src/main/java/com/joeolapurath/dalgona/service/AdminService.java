package com.joeolapurath.dalgona.service;

import com.joeolapurath.dalgona.dto.*;
import com.joeolapurath.dalgona.model.*;
import com.joeolapurath.dalgona.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

@Service
public class AdminService {

    private final AccountRepository accountRepository;
    private final OrderRepository orderRepository;
    private final UmbrellaRepository umbrellaRepository;
    private final StationRepository stationRepository;
    private final StationSlotRepository stationSlotRepository;

    public AdminService(AccountRepository accountRepository,
                        OrderRepository orderRepository,
                        UmbrellaRepository umbrellaRepository,
                        StationRepository stationRepository,
                        StationSlotRepository stationSlotRepository) {
        this.accountRepository = accountRepository;
        this.orderRepository = orderRepository;
        this.umbrellaRepository = umbrellaRepository;
        this.stationRepository = stationRepository;
        this.stationSlotRepository = stationSlotRepository;
    }

    public List<AccountResponse> getAllAccounts() {
        return accountRepository.findAll().stream().map(this::toAccountResponse).toList();
    }

    @Transactional
    public AccountResponse updateAccount(Long accountId, AdminAccountUpdateRequest request) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));

        if (request.getName() != null) account.setName(request.getName());
        if (request.getAddress() != null) account.setAddress(request.getAddress());
        if (request.getPhone() != null) account.setPhone(request.getPhone());
        if (request.getAge() != null && request.getAge() >= 0) account.setAge(request.getAge());
        if (request.getRole() != null) {
            account.setRole(parseRole(request.getRole()));
        }

        return toAccountResponse(accountRepository.save(account));
    }

    @Transactional
    public void deleteAccount(Long accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        accountRepository.delete(account);
    }

    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAllByOrderByRentedAtDesc().stream().map(this::toOrderResponse).toList();
    }

    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        return toOrderResponse(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        if (order.getReturnedAt() == null) {
            throw new RuntimeException("Cannot delete an active order");
        }
        orderRepository.delete(order);
    }

    public List<UmbrellaResponse> getAllUmbrellas() {
        return umbrellaRepository.findAll().stream().map(this::toUmbrellaResponse).toList();
    }

    @Transactional
    public UmbrellaResponse createUmbrella() {
        Umbrella umbrella = Umbrella.builder().inUse(false).build();
        umbrella = umbrellaRepository.save(umbrella);
        return toUmbrellaResponse(umbrella);
    }

    @Transactional
    public void deleteUmbrella(Long umbrellaId) {
        Umbrella umbrella = umbrellaRepository.findById(umbrellaId)
                .orElseThrow(() -> new RuntimeException("Umbrella not found"));
        if (umbrella.getInUse()) {
            throw new RuntimeException("Cannot delete umbrella currently in use");
        }
        if (stationSlotRepository.existsByUmbrella(umbrella)) {
            throw new RuntimeException("Cannot delete umbrella assigned to a station slot");
        }
        if (orderRepository.existsByUmbrellaAndReturnedAtIsNull(umbrella)) {
            throw new RuntimeException("Cannot delete umbrella with active order");
        }
        umbrellaRepository.delete(umbrella);
    }

    public List<StationResponse> getAllStations() {
        return stationRepository.findAll().stream().map(this::toStationResponse).toList();
    }

    @Transactional
    public StationResponse createStation(String location, int capacity) {
        if (location == null || location.isBlank()) {
            throw new RuntimeException("Station location is required");
        }
        if (capacity <= 0) {
            throw new RuntimeException("Station capacity must be greater than zero");
        }

        Station station = Station.builder()
                .location(location.trim())
                .capacity(capacity)
                .build();
        station = stationRepository.save(station);

        for (int i = 1; i <= capacity; i++) {
            stationSlotRepository.save(StationSlot.builder()
                    .station(station)
                    .slotIndex(i)
                    .umbrella(null)
                    .build());
        }

        Station savedStation = stationRepository.findById(station.getStationId())
                .orElseThrow(() -> new RuntimeException("Station not found"));
        return toStationResponse(savedStation);
    }

    @Transactional
    public StationResponse updateStation(Long stationId, AdminStationUpdateRequest request) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));

        if (request.getLocation() != null && !request.getLocation().isBlank()) {
            station.setLocation(request.getLocation().trim());
        }

        if (request.getCapacity() != null) {
            int newCapacity = request.getCapacity();
            if (newCapacity <= 0) {
                throw new RuntimeException("Station capacity must be greater than zero");
            }

            int occupied = (int) stationSlotRepository.countByStationAndUmbrellaIsNotNull(station);
            if (newCapacity < occupied) {
                throw new RuntimeException("Station capacity cannot be lower than occupied slots");
            }

            int currentCapacity = station.getCapacity();
            if (newCapacity > currentCapacity) {
                for (int i = currentCapacity + 1; i <= newCapacity; i++) {
                    stationSlotRepository.save(StationSlot.builder()
                            .station(station)
                            .slotIndex(i)
                            .umbrella(null)
                            .build());
                }
            } else if (newCapacity < currentCapacity) {
                int slotsToRemove = currentCapacity - newCapacity;
                for (int i = 0; i < slotsToRemove; i++) {
                    StationSlot emptySlot = stationSlotRepository
                            .findFirstByStationAndUmbrellaIsNullOrderBySlotIndexDesc(station)
                            .orElseThrow(() -> new RuntimeException("Unable to shrink station capacity due to occupied slots"));
                    stationSlotRepository.delete(emptySlot);
                }
            }

            station.setCapacity(newCapacity);
        }

        Station savedStation = stationRepository.save(station);
        return toStationResponse(savedStation);
    }

    @Transactional
    public void deleteStation(Long stationId) {
        Station station = stationRepository.findById(stationId)
                .orElseThrow(() -> new RuntimeException("Station not found"));
        long occupied = stationSlotRepository.countByStationAndUmbrellaIsNotNull(station);
        if (occupied > 0) {
            throw new RuntimeException("Cannot delete station with umbrellas currently assigned");
        }
        stationRepository.delete(station);
    }

    private Role parseRole(String roleText) {
        try {
            return Role.valueOf(roleText.trim().toUpperCase(Locale.ROOT));
        } catch (Exception ex) {
            throw new RuntimeException("Invalid role. Use USER or ADMIN");
        }
    }

    private AccountResponse toAccountResponse(Account account) {
        Role role = account.getRole() == null ? Role.USER : account.getRole();
        return AccountResponse.builder()
                .accountId(account.getAccountId())
                .name(account.getName())
                .email(account.getEmail())
                .address(account.getAddress())
                .phone(account.getPhone())
                .age(account.getAge())
                .role(role.name())
                .build();
    }

    private OrderResponse toOrderResponse(Order order) {
        OrderResponse.OrderResponseBuilder builder = OrderResponse.builder()
                .orderId(order.getOrderId())
                .umbrellaId(order.getUmbrella().getUmbrellaId())
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

    private UmbrellaResponse toUmbrellaResponse(Umbrella umbrella) {
        return UmbrellaResponse.builder()
                .umbrellaId(umbrella.getUmbrellaId())
                .inUse(Boolean.TRUE.equals(umbrella.getInUse()))
                .build();
    }

    private StationResponse toStationResponse(Station station) {
        int occupied = (int) stationSlotRepository.countByStationAndUmbrellaIsNotNull(station);
        int available = station.getCapacity() - occupied;
        return StationResponse.builder()
                .stationId(station.getStationId())
                .location(station.getLocation())
                .capacity(station.getCapacity())
                .occupied(occupied)
                .available(available)
                .build();
    }
}
