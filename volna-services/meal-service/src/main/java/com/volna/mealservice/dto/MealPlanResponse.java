package com.volna.mealservice.dto;
import com.volna.mealservice.entity.*;
import java.math.BigDecimal;
import java.time.*;
import java.util.*;
public record MealPlanResponse(
    UUID id, String name, String description, MealPlanPricingModel pricingModel,
    BigDecimal basePrice, String currency, Integer mealsPerDay, LocalDate firstDeliveryDate,
    MealPlanLifecycle lifecycle, MealPlanStatus status, long subscribers,
    List<DayOfWeek> deliveryDays, List<MealPlanItemResponse> menuItems
) {}
