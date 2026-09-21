package com.volna.restaurantservice.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(
        name = "orders",
        indexes = {
                @Index(name = "idx_orders_restaurant_id", columnList = "restaurant_id"),
                @Index(name = "idx_orders_restaurant_status", columnList = "restaurant_id,status"),
                @Index(name = "idx_orders_created_at", columnList = "created_at")
        }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    private UUID id;

    @Column(name = "restaurant_id", nullable = false)
    private UUID restaurantId;

    @Column(name = "customer_id")
    private UUID customerId;

    @Column(name = "customer_name", nullable = false, length = 150)
    private String customerName;

    @Column(name = "customer_phone", length = 30)
    private String customerPhone;

    @Column(name = "order_type", nullable = false, length = 30)
    private String orderType;

    @Column(name = "delivery_address", length = 500)
    private String deliveryAddress;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OrderStatus status;

    @Column(name = "payment_status", nullable = false, length = 30)
    private String paymentStatus;

    @Column(name = "rejection_reason", length = 500)
    private String rejectionReason;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    protected void onCreate() {

        OffsetDateTime now =
                OffsetDateTime.now(ZoneOffset.UTC);

        if (id == null) {
            id = UUID.randomUUID();
        }

        if (createdAt == null) {
            createdAt = now;
        }

        updatedAt = now;

        if (status == null) {
            status = OrderStatus.NEW;
        }

        if (paymentStatus == null || paymentStatus.isBlank()) {
            paymentStatus = "PENDING";
        }
    }

    @PreUpdate
    protected void onUpdate() {

        updatedAt =
                OffsetDateTime.now(ZoneOffset.UTC);
    }
}