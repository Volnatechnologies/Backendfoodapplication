package com.caloryhive.business.catering.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "booking_custom_options", indexes = {
        @Index(name = "idx_booking_custom_opt_booking_id", columnList = "booking_id"),
        @Index(name = "idx_booking_custom_opt_option_id", columnList = "custom_option_id")
})
@Getter
@Setter
@NoArgsConstructor
public class BookingCustomOption {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    private CateringBooking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "custom_option_id", nullable = false)
    private CateringCustomOption customOption;
}

