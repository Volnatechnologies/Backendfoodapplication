package com.volna.mealservice.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.UUID;

@Entity
@Table(name="meal_delivery_records")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class MealDeliveryRecord {
    @Id private UUID id;
    @Column(name="meal_plan_id", nullable=false) private UUID mealPlanId;
    @Column(name="subscription_id") private UUID subscriptionId;
    @Column(name="scheduled_date", nullable=false) private LocalDate scheduledDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private DeliveryStatus status;
    @Column(name="delivered_at") private OffsetDateTime deliveredAt;
    @Column(name="failure_reason", length=500) private String failureReason;
    @Column(name="created_at", nullable=false) private OffsetDateTime createdAt;
    @PrePersist void prePersist() {
        if (id == null) id=UUID.randomUUID();
        createdAt=OffsetDateTime.now(ZoneOffset.UTC);
        if (status == null) status=DeliveryStatus.SCHEDULED;
    }
}
