package com.volna.mealservice.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.UUID;

@Entity
@Table(name="meal_plan_items")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class MealPlanItem {
    @Id private UUID id;
    @Column(name="meal_plan_id", nullable=false) private UUID mealPlanId;
    @Column(nullable=false, length=150) private String name;
    @Column(length=500) private String description;
    @Column(name="image_url", length=1000) private String imageUrl;
    @Enumerated(EnumType.STRING) @Column(name="dietary_type", nullable=false) private MealDietaryType dietaryType;
    @Column(nullable=false) private boolean active;
    @Column(name="created_at", nullable=false) private OffsetDateTime createdAt;
    @PrePersist void prePersist() {
        if (id == null) id = UUID.randomUUID();
        createdAt = OffsetDateTime.now(ZoneOffset.UTC);
        if (dietaryType == null) dietaryType = MealDietaryType.GENERAL;
        active = true;
    }
}
