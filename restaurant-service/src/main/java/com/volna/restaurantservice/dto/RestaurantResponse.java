package com.volna.restaurantservice.dto;
import com.volna.restaurantservice.entity.RestaurantStatus; import java.math.BigDecimal; import java.time.*; import java.util.UUID;
public record RestaurantResponse(
        UUID id,
        UUID ownerId,
        String name,
        String description,
        String phone,
        String email,
        String address,
        BigDecimal latitude,
        BigDecimal longitude,
        LocalTime openingTime,
        LocalTime closingTime,
        RestaurantStatus status,
        OffsetDateTime createdAt,
        OffsetDateTime updatedAt
) {}