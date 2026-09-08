package com.volna.mealservice.entity;
import lombok.*;
import java.io.Serializable;
import java.time.DayOfWeek;
import java.util.UUID;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class MealPlanDeliveryDayId implements Serializable {
    private UUID mealPlanId;
    private DayOfWeek dayOfWeek;
}
