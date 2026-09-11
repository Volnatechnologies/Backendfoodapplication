package com.caloryhive.business.catering.entity;

import com.caloryhive.business.common.AuditEntity;
import com.caloryhive.business.common.enums.BookingStatus;
import com.caloryhive.business.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "catering_bookings",
        indexes = {
                @Index(name = "idx_booking_code", columnList = "booking_code", unique = true),
                @Index(name = "idx_booking_event_date", columnList = "event_date"),
                @Index(name = "idx_booking_status", columnList = "status"),
                @Index(name = "idx_booking_created_at", columnList = "created_at"),
                @Index(name = "idx_booking_event_name", columnList = "event_name")
        })
@Getter
@Setter
@NoArgsConstructor
public class CateringBooking extends AuditEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    @Column(name = "booking_code", unique = true, length = 20)
    private String bookingCode;

    @Column(name = "event_name", nullable = false)
    private String eventName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id")
    private CateringEventType eventType;

    @Column(name = "event_date")
    private LocalDate eventDate;

    @Column(name = "event_time")
    private LocalTime eventTime;

    @Column(name = "guest_count")
    private Integer guestCount;

    @Column(name = "venue_address", columnDefinition = "TEXT")
    private String venueAddress;

    @Column(name = "special_instructions", columnDefinition = "TEXT")
    private String specialInstructions;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "menu_package_id")
    private CateringMenuPackage menuPackage;

    @Column(name = "base_price", precision = 19, scale = 4, nullable = false)
    private BigDecimal basePrice = BigDecimal.ZERO;

    @Column(name = "custom_options_total", precision = 19, scale = 4, nullable = false)
    private BigDecimal customOptionsTotal = BigDecimal.ZERO;

    @Column(name = "subtotal", precision = 19, scale = 4, nullable = false)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Column(name = "service_fee", precision = 19, scale = 4, nullable = false)
    private BigDecimal serviceFee = BigDecimal.ZERO;

    @Column(name = "tax", precision = 19, scale = 4, nullable = false)
    private BigDecimal tax = BigDecimal.ZERO;

    @Column(name = "final_total", precision = 19, scale = 4, nullable = false)
    private BigDecimal finalTotal = BigDecimal.ZERO;

    @Column(name = "deposit_percentage", precision = 5, scale = 4, nullable = false)
    private BigDecimal depositPercentage = new BigDecimal("0.30");

    @Column(name = "deposit_required", precision = 19, scale = 4, nullable = false)
    private BigDecimal depositRequired = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    private BookingStatus status = BookingStatus.DRAFT;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by_id")
    private User createdBy;

    @Version
    @Column(name = "version")
    private Long version;

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingMenuPackage> bookingMenuPackages = new ArrayList<>();

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingCustomOption> bookingCustomOptions = new ArrayList<>();

    @com.fasterxml.jackson.annotation.JsonIgnore
    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<BookingStatusHistory> statusHistory = new ArrayList<>();

    public void addCustomOption(CateringCustomOption customOption) {
        BookingCustomOption option = new BookingCustomOption();
        option.setBooking(this);
        option.setCustomOption(customOption);
        this.bookingCustomOptions.add(option);
    }

    public void addMenuPackage(CateringMenuPackage menuPackage) {
        BookingMenuPackage pkg = new BookingMenuPackage();
        pkg.setBooking(this);
        pkg.setMenuPackage(menuPackage);
        this.bookingMenuPackages.add(pkg);
    }
}


