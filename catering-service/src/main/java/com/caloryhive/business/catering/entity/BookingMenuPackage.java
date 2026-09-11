package com.caloryhive.business.catering.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "booking_menu_packages", indexes = {
        @Index(name = "idx_booking_menu_pkg_booking_id", columnList = "booking_id"),
        @Index(name = "idx_booking_menu_pkg_package_id", columnList = "menu_package_id")
})
@Getter
@Setter
@NoArgsConstructor
public class BookingMenuPackage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    private CateringBooking booking;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_package_id", nullable = false)
    private CateringMenuPackage menuPackage;
}

