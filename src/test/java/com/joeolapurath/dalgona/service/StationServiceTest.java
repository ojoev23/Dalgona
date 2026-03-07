package com.joeolapurath.dalgona.service;

import com.joeolapurath.dalgona.dto.StationResponse;
import com.joeolapurath.dalgona.model.Station;
import com.joeolapurath.dalgona.model.StationSlot;
import com.joeolapurath.dalgona.repository.StationRepository;
import com.joeolapurath.dalgona.repository.StationSlotRepository;
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
class StationServiceTest {

    @Mock
    private StationRepository stationRepository;

    @Mock
    private StationSlotRepository stationSlotRepository;

    @InjectMocks
    private StationService stationService;

    private Station testStation;

    @BeforeEach
    void setUp() {
        testStation = Station.builder()
                .stationId(1L)
                .location("Downtown")
                .capacity(5)
                .slots(new ArrayList<>())
                .build();
    }

    @Test
    void getAllStations_shouldReturnAllStations() {
        Station station2 = Station.builder()
                .stationId(2L)
                .location("Uptown")
                .capacity(3)
                .slots(new ArrayList<>())
                .build();

        when(stationRepository.findAll()).thenReturn(List.of(testStation, station2));
        when(stationSlotRepository.countByStationAndUmbrellaIsNotNull(testStation)).thenReturn(2L);
        when(stationSlotRepository.countByStationAndUmbrellaIsNotNull(station2)).thenReturn(1L);

        List<StationResponse> results = stationService.getAllStations();

        assertEquals(2, results.size());

        StationResponse response1 = results.get(0);
        assertEquals(1L, response1.getStationId());
        assertEquals("Downtown", response1.getLocation());
        assertEquals(5, response1.getCapacity());
        assertEquals(2, response1.getOccupied());
        assertEquals(3, response1.getAvailable());

        StationResponse response2 = results.get(1);
        assertEquals(2L, response2.getStationId());
        assertEquals("Uptown", response2.getLocation());
        assertEquals(3, response2.getCapacity());
        assertEquals(1, response2.getOccupied());
        assertEquals(2, response2.getAvailable());
    }

    @Test
    void getAllStations_shouldReturnEmptyList_whenNoStations() {
        when(stationRepository.findAll()).thenReturn(List.of());

        List<StationResponse> results = stationService.getAllStations();

        assertTrue(results.isEmpty());
    }

    @Test
    void getStationById_shouldReturnStation_whenFound() {
        when(stationRepository.findById(1L)).thenReturn(Optional.of(testStation));
        when(stationSlotRepository.countByStationAndUmbrellaIsNotNull(testStation)).thenReturn(3L);

        StationResponse response = stationService.getStationById(1L);

        assertNotNull(response);
        assertEquals(1L, response.getStationId());
        assertEquals("Downtown", response.getLocation());
        assertEquals(5, response.getCapacity());
        assertEquals(3, response.getOccupied());
        assertEquals(2, response.getAvailable());
    }

    @Test
    void getStationById_shouldThrowException_whenNotFound() {
        when(stationRepository.findById(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                stationService.getStationById(999L));

        assertEquals("Station not found", exception.getMessage());
    }

    @Test
    void createStation_shouldCreateStationWithEmptySlots() {
        Station savedStation = Station.builder()
                .stationId(1L)
                .location("Park")
                .capacity(3)
                .slots(new ArrayList<>())
                .build();

        when(stationRepository.save(any(Station.class))).thenReturn(savedStation);
        when(stationSlotRepository.save(any(StationSlot.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(stationRepository.findById(1L)).thenReturn(Optional.of(savedStation));
        when(stationSlotRepository.countByStationAndUmbrellaIsNotNull(savedStation)).thenReturn(0L);

        StationResponse response = stationService.createStation("Park", 3);

        assertNotNull(response);
        assertEquals("Park", response.getLocation());
        assertEquals(3, response.getCapacity());
        assertEquals(0, response.getOccupied());
        assertEquals(3, response.getAvailable());

        // Verify that 3 slots were created
        verify(stationSlotRepository, times(3)).save(any(StationSlot.class));
    }

    @Test
    void createStation_shouldCreateCorrectSlotIndices() {
        Station savedStation = Station.builder()
                .stationId(1L)
                .location("Park")
                .capacity(5)
                .slots(new ArrayList<>())
                .build();

        when(stationRepository.save(any(Station.class))).thenReturn(savedStation);
        when(stationSlotRepository.save(any(StationSlot.class))).thenAnswer(invocation -> {
            StationSlot slot = invocation.getArgument(0);
            return slot;
        });
        when(stationRepository.findById(1L)).thenReturn(Optional.of(savedStation));
        when(stationSlotRepository.countByStationAndUmbrellaIsNotNull(savedStation)).thenReturn(0L);

        stationService.createStation("Park", 5);

        verify(stationSlotRepository, times(5)).save(argThat(slot ->
                slot.getStation().equals(savedStation) &&
                        slot.getSlotIndex() >= 1 &&
                        slot.getSlotIndex() <= 5 &&
                        slot.getUmbrella() == null
        ));
    }

    @Test
    void getStationEntity_shouldReturnStation_whenFound() {
        when(stationRepository.findById(1L)).thenReturn(Optional.of(testStation));

        Station result = stationService.getStationEntity(1L);

        assertNotNull(result);
        assertEquals(1L, result.getStationId());
        assertEquals("Downtown", result.getLocation());
    }

    @Test
    void getStationEntity_shouldThrowException_whenNotFound() {
        when(stationRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () ->
                stationService.getStationEntity(999L));
    }
}

