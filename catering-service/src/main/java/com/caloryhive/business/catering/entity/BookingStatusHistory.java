package com.caloryhive.business.catering.entity;

import com.caloryhive.business.common.enums.BookingStatus;
import com.caloryhive.business.user.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "booking_status_history", indexes = {
        @Index(name = "idx_status_history_booking_id", columnList = "booking_id"),
        @Index(name = "idx_status_history_changed_at", columnList = "changed_at")
})
@Getter
@Setter
@NoArgsConstructor
public class BookingStatusHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id", nullable = false)
    @JsonIgnore
    private CateringBooking booking;

    @Enumerated(EnumType.STRING)
    @Column(name = "old_status", length = 40)
    private BookingStatus oldStatus;

    @Enumerated(EnumType.STRING)
    @Column(name = "new_status", length = 40, nullable = false)
    private BookingStatus newStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "changed_by_id")
    private User changedBy;

    @Column(name = "changed_at", nullable = false)
    private LocalDateTime changedAt = LocalDateTime.now();

    @Column(name = "comment", columnDefinition = "TEXT")
    private String comment;
}


