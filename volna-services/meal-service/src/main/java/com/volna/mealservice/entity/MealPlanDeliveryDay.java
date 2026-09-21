package com.volna.mealservice.entity;
import jakarta.persistence.*;
import lombok.*;
import java.time.DayOfWeek;
import java.util.UUID;

@Entity
@Table(name="meal_plan_delivery_days")
@IdClass(MealPlanDeliveryDayId.class)
@Getter @NoArgsConstructor
public class MealPlanDeliveryDay {
    @Id @Column(name="meal_plan_id") private UUID mealPlanId;
    @Id @Enumerated(EnumType.STRING) @Column(name="day_of_week") private DayOfWeek dayOfWeek;
    public MealPlanDeliveryDay(UUID mealPlanId, DayOfWeek dayOfWeek) {
        this.mealPlanId = mealPlanId; this.dayOfWeek = dayOfWeek;
    }
}
