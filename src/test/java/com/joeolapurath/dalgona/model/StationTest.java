package com.joeolapurath.dalgona.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StationTest {

    @Test
    void getOccupiedCount_shouldReturnZero_whenAllSlotsEmpty() {
        Station station = Station.builder()
                .stationId(1L)
                .capacity(3)
                .location("Test")
                .slots(new ArrayList<>(List.of(
                        StationSlot.builder().slotIndex(1).umbrella(null).build(),
                        StationSlot.builder().slotIndex(2).umbrella(null).build(),
                        StationSlot.builder().slotIndex(3).umbrella(null).build()
                )))
                .build();

        assertEquals(0, station.getOccupiedCount());
    }

    @Test
    void getOccupiedCount_shouldCountOccupiedSlots() {
        Umbrella umbrella1 = Umbrella.builder().umbrellaId(1L).build();
        Umbrella umbrella2 = Umbrella.builder().umbrellaId(2L).build();

        Station station = Station.builder()
                .stationId(1L)
                .capacity(3)
                .location("Test")
                .slots(new ArrayList<>(List.of(
                        StationSlot.builder().slotIndex(1).umbrella(umbrella1).build(),
                        StationSlot.builder().slotIndex(2).umbrella(null).build(),
                        StationSlot.builder().slotIndex(3).umbrella(umbrella2).build()
                )))
                .build();

        assertEquals(2, station.getOccupiedCount());
    }

    @Test
    void getAvailableCount_shouldReturnCapacityMinusOccupied() {
        Umbrella umbrella = Umbrella.builder().umbrellaId(1L).build();

        Station station = Station.builder()
                .stationId(1L)
                .capacity(5)
                .location("Test")
                .slots(new ArrayList<>(List.of(
                        StationSlot.builder().slotIndex(1).umbrella(umbrella).build(),
                        StationSlot.builder().slotIndex(2).umbrella(null).build(),
                        StationSlot.builder().slotIndex(3).umbrella(null).build()
                )))
                .build();

        assertEquals(4, station.getAvailableCount());
    }

    @Test
    void getAvailableCount_shouldReturnFull_whenAllEmpty() {
        Station station = Station.builder()
                .stationId(1L)
                .capacity(3)
                .location("Test")
                .slots(new ArrayList<>())
                .build();

        assertEquals(3, station.getAvailableCount());
    }

    @Test
    void getAvailableCount_shouldReturnZero_whenAllFull() {
        Umbrella u1 = Umbrella.builder().umbrellaId(1L).build();
        Umbrella u2 = Umbrella.builder().umbrellaId(2L).build();
        Umbrella u3 = Umbrella.builder().umbrellaId(3L).build();

        Station station = Station.builder()
                .stationId(1L)
                .capacity(3)
                .location("Test")
                .slots(new ArrayList<>(List.of(
                        StationSlot.builder().slotIndex(1).umbrella(u1).build(),
                        StationSlot.builder().slotIndex(2).umbrella(u2).build(),
                        StationSlot.builder().slotIndex(3).umbrella(u3).build()
                )))
                .build();

        assertEquals(0, station.getAvailableCount());
    }
}

