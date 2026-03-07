package com.joeolapurath.dalgona.service;

import com.joeolapurath.dalgona.dto.OrderResponse;
import com.joeolapurath.dalgona.model.*;
import com.joeolapurath.dalgona.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UmbrellaServiceTest {

    @Mock
    private UmbrellaRepository umbrellaRepository;

    @Mock
    private StationSlotRepository stationSlotRepository;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private StationRepository stationRepository;

    @InjectMocks
    private UmbrellaService umbrellaService;

    private Account testAccount;
    private Station pickupStation;
    private Station returnStation;
    private Umbrella testUmbrella;
    private StationSlot occupiedSlot;
    private StationSlot emptySlot;

    @BeforeEach
    void setUp() {
        testAccount = Account.builder()
                .accountId(1L)
                .email("john@example.com")
                .build();

        testUmbrella = Umbrella.builder()
                .umbrellaId(1L)
                .name("Blue Umbrella")
                .inUse(false)
                .build();

        occupiedSlot = StationSlot.builder()
                .slotId(1L)
                .slotIndex(1)
                .umbrella(testUmbrella)
                .build();

        emptySlot = StationSlot.builder()
                .slotId(2L)
                .slotIndex(2)
                .umbrella(null)
                .build();

        pickupStation = Station.builder()
                .stationId(1L)
                .location("Downtown")
                .capacity(5)
                .slots(new ArrayList<>(List.of(occupiedSlot, emptySlot)))
                .build();

        occupiedSlot.setStation(pickupStation);
        emptySlot.setStation(pickupStation);

        returnStation = Station.builder()
                .stationId(2L)
                .location("Uptown")
                .capacity(5)
                .slots(new ArrayList<>())
                .build();
    }

    // ========== RENT UMBRELLA TESTS ==========

    @Test
    void rentUmbrella_shouldSucceed_whenUmbrellaAvailable() {
        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(orderRepository.existsByAccountAndReturnedAtIsNull(testAccount)).thenReturn(false);
        when(stationRepository.findById(1L)).thenReturn(Optional.of(pickupStation));
        when(stationSlotRepository.save(any(StationSlot.class))).thenAnswer(i -> i.getArgument(0));
        when(umbrellaRepository.save(any(Umbrella.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order order = invocation.getArgument(0);
            order.setOrderId(1L);
            return order;
        });

        OrderResponse response = umbrellaService.rentUmbrella("john@example.com", 1L);

        assertNotNull(response);
        assertEquals(1L, response.getUmbrellaId());
        assertEquals("Blue Umbrella", response.getUmbrellaName());
        assertEquals(1L, response.getPickupStationId());
        assertEquals("Downtown", response.getPickupStationLocation());
        assertTrue(response.isActive());
        assertNull(response.getReturnedAt());
        assertNull(response.getReturnStationId());

        // Verify umbrella was removed from slot
        assertNull(occupiedSlot.getUmbrella());
        verify(stationSlotRepository).save(occupiedSlot);

        // Verify umbrella marked as in use
        assertTrue(testUmbrella.getInUse());
        verify(umbrellaRepository).save(testUmbrella);

        // Verify order was created
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void rentUmbrella_shouldThrow_whenAccountNotFound() {
        when(accountRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                umbrellaService.rentUmbrella("missing@example.com", 1L));

        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    void rentUmbrella_shouldThrow_whenUserAlreadyHasActiveRental() {
        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(orderRepository.existsByAccountAndReturnedAtIsNull(testAccount)).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                umbrellaService.rentUmbrella("john@example.com", 1L));

        assertEquals("You already have an active rental. Return it before renting another.", exception.getMessage());
        verify(stationRepository, never()).findById(anyLong());
    }

    @Test
    void rentUmbrella_shouldThrow_whenStationNotFound() {
        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(orderRepository.existsByAccountAndReturnedAtIsNull(testAccount)).thenReturn(false);
        when(stationRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                umbrellaService.rentUmbrella("john@example.com", 999L));

        assertEquals("Station not found", exception.getMessage());
    }

    @Test
    void rentUmbrella_shouldThrow_whenNoUmbrellasAvailableAtStation() {
        Station emptyStation = Station.builder()
                .stationId(3L)
                .location("Empty Station")
                .capacity(5)
                .slots(new ArrayList<>(List.of(
                        StationSlot.builder().slotId(10L).slotIndex(1).umbrella(null).build(),
                        StationSlot.builder().slotId(11L).slotIndex(2).umbrella(null).build()
                )))
                .build();

        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(orderRepository.existsByAccountAndReturnedAtIsNull(testAccount)).thenReturn(false);
        when(stationRepository.findById(3L)).thenReturn(Optional.of(emptyStation));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                umbrellaService.rentUmbrella("john@example.com", 3L));

        assertEquals("No umbrellas available at this station", exception.getMessage());
    }

    // ========== RETURN UMBRELLA TESTS ==========

    @Test
    void returnUmbrella_shouldSucceed_whenValidReturn() {
        testUmbrella.setInUse(true);

        Order activeOrder = Order.builder()
                .orderId(1L)
                .account(testAccount)
                .umbrella(testUmbrella)
                .pickupStation(pickupStation)
                .build();

        StationSlot emptyReturnSlot = StationSlot.builder()
                .slotId(5L)
                .slotIndex(1)
                .station(returnStation)
                .umbrella(null)
                .build();

        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(umbrellaRepository.findById(1L)).thenReturn(Optional.of(testUmbrella));
        when(orderRepository.findByUmbrellaAndReturnedAtIsNull(testUmbrella)).thenReturn(Optional.of(activeOrder));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(returnStation));
        when(stationSlotRepository.findFirstByStationAndUmbrellaIsNullOrderBySlotIndexAsc(returnStation))
                .thenReturn(Optional.of(emptyReturnSlot));
        when(stationSlotRepository.save(any(StationSlot.class))).thenAnswer(i -> i.getArgument(0));
        when(umbrellaRepository.save(any(Umbrella.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = umbrellaService.returnUmbrella("john@example.com", 2L, 1L);

        assertNotNull(response);
        assertFalse(response.isActive());
        assertNotNull(response.getReturnedAt());
        assertEquals(2L, response.getReturnStationId());
        assertEquals("Uptown", response.getReturnStationLocation());

        // Verify umbrella was placed in slot
        assertEquals(testUmbrella, emptyReturnSlot.getUmbrella());
        verify(stationSlotRepository).save(emptyReturnSlot);

        // Verify umbrella marked as not in use
        assertFalse(testUmbrella.getInUse());
        verify(umbrellaRepository).save(testUmbrella);

        // Verify order was completed
        assertEquals(returnStation, activeOrder.getReturnStation());
        assertNotNull(activeOrder.getReturnedAt());
        verify(orderRepository).save(activeOrder);
    }

    @Test
    void returnUmbrella_shouldThrow_whenAccountNotFound() {
        when(accountRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                umbrellaService.returnUmbrella("missing@example.com", 2L, 1L));

        assertEquals("Account not found", exception.getMessage());
    }

    @Test
    void returnUmbrella_shouldThrow_whenUmbrellaNotFound() {
        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(umbrellaRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                umbrellaService.returnUmbrella("john@example.com", 2L, 999L));

        assertEquals("Umbrella not found", exception.getMessage());
    }

    @Test
    void returnUmbrella_shouldThrow_whenUmbrellaNotCurrentlyRented() {
        testUmbrella.setInUse(false);

        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(umbrellaRepository.findById(1L)).thenReturn(Optional.of(testUmbrella));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                umbrellaService.returnUmbrella("john@example.com", 2L, 1L));

        assertEquals("This umbrella is not currently rented", exception.getMessage());
    }

    @Test
    void returnUmbrella_shouldThrow_whenNoActiveRentalForUmbrella() {
        testUmbrella.setInUse(true);

        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(umbrellaRepository.findById(1L)).thenReturn(Optional.of(testUmbrella));
        when(orderRepository.findByUmbrellaAndReturnedAtIsNull(testUmbrella)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                umbrellaService.returnUmbrella("john@example.com", 2L, 1L));

        assertEquals("No active rental found for this umbrella", exception.getMessage());
    }

    @Test
    void returnUmbrella_shouldThrow_whenUmbrellaNotRentedByUser() {
        testUmbrella.setInUse(true);

        Account otherAccount = Account.builder()
                .accountId(2L)
                .email("other@example.com")
                .build();

        Order otherOrder = Order.builder()
                .orderId(1L)
                .account(otherAccount)
                .umbrella(testUmbrella)
                .pickupStation(pickupStation)
                .build();

        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(umbrellaRepository.findById(1L)).thenReturn(Optional.of(testUmbrella));
        when(orderRepository.findByUmbrellaAndReturnedAtIsNull(testUmbrella)).thenReturn(Optional.of(otherOrder));

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                umbrellaService.returnUmbrella("john@example.com", 2L, 1L));

        assertEquals("This umbrella is not rented by you", exception.getMessage());
    }

    @Test
    void returnUmbrella_shouldThrow_whenReturnStationNotFound() {
        testUmbrella.setInUse(true);

        Order activeOrder = Order.builder()
                .orderId(1L)
                .account(testAccount)
                .umbrella(testUmbrella)
                .pickupStation(pickupStation)
                .build();

        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(umbrellaRepository.findById(1L)).thenReturn(Optional.of(testUmbrella));
        when(orderRepository.findByUmbrellaAndReturnedAtIsNull(testUmbrella)).thenReturn(Optional.of(activeOrder));
        when(stationRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                umbrellaService.returnUmbrella("john@example.com", 999L, 1L));

        assertEquals("Station not found", exception.getMessage());
    }

    @Test
    void returnUmbrella_shouldThrow_whenNoEmptySlotsAtStation() {
        testUmbrella.setInUse(true);

        Order activeOrder = Order.builder()
                .orderId(1L)
                .account(testAccount)
                .umbrella(testUmbrella)
                .pickupStation(pickupStation)
                .build();

        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(umbrellaRepository.findById(1L)).thenReturn(Optional.of(testUmbrella));
        when(orderRepository.findByUmbrellaAndReturnedAtIsNull(testUmbrella)).thenReturn(Optional.of(activeOrder));
        when(stationRepository.findById(2L)).thenReturn(Optional.of(returnStation));
        when(stationSlotRepository.findFirstByStationAndUmbrellaIsNullOrderBySlotIndexAsc(returnStation))
                .thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                umbrellaService.returnUmbrella("john@example.com", 2L, 1L));

        assertEquals("No empty slots available at this station", exception.getMessage());
    }

    @Test
    void returnUmbrella_shouldReturnToSameStation() {
        testUmbrella.setInUse(true);

        Order activeOrder = Order.builder()
                .orderId(1L)
                .account(testAccount)
                .umbrella(testUmbrella)
                .pickupStation(pickupStation)
                .build();

        StationSlot emptySlotAtPickup = StationSlot.builder()
                .slotId(10L)
                .slotIndex(3)
                .station(pickupStation)
                .umbrella(null)
                .build();

        when(accountRepository.findByEmail("john@example.com")).thenReturn(Optional.of(testAccount));
        when(umbrellaRepository.findById(1L)).thenReturn(Optional.of(testUmbrella));
        when(orderRepository.findByUmbrellaAndReturnedAtIsNull(testUmbrella)).thenReturn(Optional.of(activeOrder));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(pickupStation));
        when(stationSlotRepository.findFirstByStationAndUmbrellaIsNullOrderBySlotIndexAsc(pickupStation))
                .thenReturn(Optional.of(emptySlotAtPickup));
        when(stationSlotRepository.save(any(StationSlot.class))).thenAnswer(i -> i.getArgument(0));
        when(umbrellaRepository.save(any(Umbrella.class))).thenAnswer(i -> i.getArgument(0));
        when(orderRepository.save(any(Order.class))).thenAnswer(i -> i.getArgument(0));

        OrderResponse response = umbrellaService.returnUmbrella("john@example.com", 1L, 1L);

        assertNotNull(response);
        assertFalse(response.isActive());
        assertEquals(1L, response.getPickupStationId());
        assertEquals(1L, response.getReturnStationId());
    }
}

