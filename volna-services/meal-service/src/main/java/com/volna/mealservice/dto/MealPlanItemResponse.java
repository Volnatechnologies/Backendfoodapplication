package com.volna.mealservice.dto;
import com.volna.mealservice.entity.MealDietaryType;
import java.util.UUID;
public record MealPlanItemResponse(
    UUID id, String name, String description, String imageUrl,
    MealDietaryType dietaryType, boolean active
) {}
