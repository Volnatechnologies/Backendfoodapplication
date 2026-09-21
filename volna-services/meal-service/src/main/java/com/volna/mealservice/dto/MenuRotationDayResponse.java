package com.volna.mealservice.dto;
import java.time.LocalDate;
import java.util.List;
public record MenuRotationDayResponse(
    LocalDate date, String day, boolean deliveryDay,
    List<MealPlanRotationResponse> plans
) {}
