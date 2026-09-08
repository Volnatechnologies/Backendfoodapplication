package com.volna.mealservice.dto;
import jakarta.validation.constraints.Size;
public record MealSubscriptionCancelRequest(@Size(max=500) String reason) {}
