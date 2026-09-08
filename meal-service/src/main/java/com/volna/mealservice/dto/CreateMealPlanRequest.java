package com.volna.mealservice.dto;
import com.volna.mealservice.entity.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;

public record CreateMealPlanRequest(
    @NotBlank @Size(max=150) String name,
    @Size(max=1000) String description,
    @NotNull MealPlanPricingModel pricingModel,
    @NotNull @DecimalMin("0.00") BigDecimal basePrice,
    @NotEmpty List<DayOfWeek> deliveryDays,
    @NotNull @Min(1) @Max(10) Integer mealsPerDay,
    LocalDate firstDeliveryDate,
    @NotNull MealPlanLifecycle lifecycle,
    @Valid List<MealPlanItemRequest> menuItems
) {}
