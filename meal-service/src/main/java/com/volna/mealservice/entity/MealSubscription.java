package com.volna.mealservice.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.UUID;

@Entity
@Table(name="meal_subscriptions")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class MealSubscription {
    @Id private UUID id;
    @Column(name="meal_plan_id", nullable=false) private UUID mealPlanId;
    @Column(name="customer_id", nullable=false) private UUID customerId;
    @Column(name="customer_name", nullable=false, length=150) private String customerName;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private MealSubscriptionStatus status;
    @Column(name="started_at", nullable=false) private LocalDate startedAt;
    @Column(name="cancelled_at") private LocalDate cancelledAt;
    @Column(name="cancellation_reason", length=500) private String cancellationReason;
    @Column(name="created_at", nullable=false) private OffsetDateTime createdAt;
    @Column(name="updated_at", nullable=false) private OffsetDateTime updatedAt;
    @PrePersist void prePersist() {
        if (id == null) id = UUID.randomUUID();
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        createdAt=now; updatedAt=now;
        if (status == null) status=MealSubscriptionStatus.ACTIVE;
        if (startedAt == null) startedAt=LocalDate.now();
    }
    @PreUpdate void preUpdate() { updatedAt=OffsetDateTime.now(ZoneOffset.UTC); }
}
