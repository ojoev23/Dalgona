package com.joeolapurath.dalgona.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    @Column(nullable = false)
    private LocalDateTime rentedAt;

    private LocalDateTime returnedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id", nullable = false)
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "umbrella_id", nullable = false)
    private Umbrella umbrella;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pickup_station_id", nullable = false)
    private Station pickupStation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "return_station_id")
    private Station returnStation;
}
