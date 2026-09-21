package com.volna.mealservice.dto;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import java.util.UUID;
public record MealSubscriptionCreateRequest(
    @NotNull UUID customerId,
    @NotBlank @Size(max=150) String customerName,
    LocalDate startedAt
) {}
