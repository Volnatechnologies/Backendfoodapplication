package com.volna.cateringservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "catering_event_custom_options")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CateringEventCustomOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "event_id", nullable = false)
    private CateringEvent event;

    @Column(name = "option_name", nullable = false)
    private String optionName;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;
}
