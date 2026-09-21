package com.volna.mealservice.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.UUID;

@Entity
@Table(name = "meal_plans")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class MealPlan {
    @Id private UUID id;
    @Column(name="owner_id", nullable=false) private UUID ownerId;
    @Column(nullable=false, length=150) private String name;
    @Column(length=1000) private String description;
    @Enumerated(EnumType.STRING) @Column(name="pricing_model", nullable=false) private MealPlanPricingModel pricingModel;
    @Column(name="base_price", nullable=false, precision=12, scale=2) private BigDecimal basePrice;
    @Column(nullable=false, length=3) private String currency;
    @Column(name="meals_per_day", nullable=false) private Integer mealsPerDay;
    @Column(name="first_delivery_date") private LocalDate firstDeliveryDate;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private MealPlanLifecycle lifecycle;
    @Enumerated(EnumType.STRING) @Column(nullable=false) private MealPlanStatus status;
    @Column(nullable=false) private boolean active;
    @Column(name="created_at", nullable=false) private OffsetDateTime createdAt;
    @Column(name="updated_at", nullable=false) private OffsetDateTime updatedAt;

    @PrePersist void prePersist() {
        if (id == null) id = UUID.randomUUID();
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        createdAt = now; updatedAt = now;
        if (currency == null) currency = "USD";
        if (mealsPerDay == null) mealsPerDay = 1;
        if (lifecycle == null) lifecycle = MealPlanLifecycle.ONGOING;
        if (status == null) status = MealPlanStatus.DRAFT;
        active = true;
    }
    @PreUpdate void preUpdate() { updatedAt = OffsetDateTime.now(ZoneOffset.UTC); }
}
