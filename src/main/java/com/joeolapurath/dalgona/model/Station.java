package com.joeolapurath.dalgona.model;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "stations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Station {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long stationId;

    @Column(nullable = false)
    private int capacity;

    @Column(nullable = false)
    private String location;

    @OneToMany(mappedBy = "station", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("slotIndex ASC")
    @Builder.Default
    private List<StationSlot> slots = new ArrayList<>();

    public int getOccupiedCount() {
        return (int) slots.stream().filter(s -> !s.isEmpty()).count();
    }

    public int getAvailableCount() {
        return capacity - getOccupiedCount();
    }
}
