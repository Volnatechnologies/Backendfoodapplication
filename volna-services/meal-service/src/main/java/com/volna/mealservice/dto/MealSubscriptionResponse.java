package com.volna.mealservice.dto;
import com.volna.mealservice.entity.MealSubscriptionStatus;
import java.time.LocalDate;
import java.util.UUID;
public record MealSubscriptionResponse(
    UUID id, UUID mealPlanId, UUID customerId, String customerName,
    MealSubscriptionStatus status, LocalDate startedAt,
    LocalDate cancelledAt, String cancellationReason
) {}
