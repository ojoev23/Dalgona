package com.joeolapurath.dalgona.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class StationSlotTest {

    @Test
    void isEmpty_shouldReturnTrue_whenNoUmbrella() {
        StationSlot slot = StationSlot.builder()
                .slotId(1L)
                .slotIndex(1)
                .umbrella(null)
                .build();

        assertTrue(slot.isEmpty());
    }

    @Test
    void isEmpty_shouldReturnFalse_whenUmbrellaPresent() {
        Umbrella umbrella = Umbrella.builder()
                .umbrellaId(1L)
                .build();

        StationSlot slot = StationSlot.builder()
                .slotId(1L)
                .slotIndex(1)
                .umbrella(umbrella)
                .build();

        assertFalse(slot.isEmpty());
    }

    @Test
    void isEmpty_shouldReturnTrue_afterRemovingUmbrella() {
        Umbrella umbrella = Umbrella.builder()
                .umbrellaId(1L)
                .build();

        StationSlot slot = StationSlot.builder()
                .slotId(1L)
                .slotIndex(1)
                .umbrella(umbrella)
                .build();

        assertFalse(slot.isEmpty());

        slot.setUmbrella(null);

        assertTrue(slot.isEmpty());
    }
}

