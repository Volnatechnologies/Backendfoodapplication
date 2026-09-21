package com.volna.mealservice.dto;
import com.volna.mealservice.entity.DeliveryStatus;
import jakarta.validation.constraints.*;
public record DeliveryStatusUpdateRequest(
    @NotNull DeliveryStatus status,
    @Size(max=500) String failureReason
) {}
