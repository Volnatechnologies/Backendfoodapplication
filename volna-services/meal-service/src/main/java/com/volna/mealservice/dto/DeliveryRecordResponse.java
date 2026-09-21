package com.volna.mealservice.dto;

import com.volna.mealservice.entity.DeliveryStatus;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public record DeliveryRecordResponse(
    UUID id,
    UUID mealPlanId,
    UUID subscriptionId,
    LocalDate scheduledDate,
    DeliveryStatus status,
    OffsetDateTime deliveredAt,
    String failureReason
) {}
