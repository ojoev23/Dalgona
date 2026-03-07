package com.joeolapurath.dalgona.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "station_slots", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"station_id", "slot_index"}),
        @UniqueConstraint(columnNames = {"umbrella_id"})
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StationSlot {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long slotId;

    @Column(name = "slot_index", nullable = false)
    private int slotIndex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "station_id", nullable = false)
    private Station station;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "umbrella_id")
    private Umbrella umbrella;

    public boolean isEmpty() {
        return umbrella == null;
    }
}


