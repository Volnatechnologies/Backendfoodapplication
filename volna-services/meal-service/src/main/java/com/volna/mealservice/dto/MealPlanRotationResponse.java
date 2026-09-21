package com.volna.mealservice.dto;
import java.util.List;
import java.util.UUID;
public record MealPlanRotationResponse(
    UUID planId, String planName, List<MealPlanItemResponse> menuItems
) {}
