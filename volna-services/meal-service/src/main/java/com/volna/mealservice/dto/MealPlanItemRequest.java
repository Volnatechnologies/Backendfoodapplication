package com.volna.mealservice.dto;
import com.volna.mealservice.entity.MealDietaryType;
import jakarta.validation.constraints.*;

public record MealPlanItemRequest(
    @NotBlank @Size(max=150) String name,
    @Size(max=500) String description,
    @Size(max=1000) String imageUrl,
    MealDietaryType dietaryType
) {}
