package com.joeolapurath.dalgona.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "umbrellas")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Umbrella {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long umbrellaId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    @Builder.Default
    private Boolean inUse = false;
}
