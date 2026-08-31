package com.volna.restaurantservice.dto;
import com.volna.restaurantservice.entity.*; import java.time.OffsetDateTime; import java.util.UUID;
public record RestaurantDocumentResponse(
        UUID id,
        UUID restaurantId,
        DocumentType type,
        String fileUrl,
        DocumentStatus status,
        OffsetDateTime uploadedAt
) {}