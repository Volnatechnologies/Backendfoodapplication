package com.volna.menuservice.dto;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;
public record MenuItemResponse(
    UUID id, UUID restaurantId, String name, String description, String imageUrl,
    BigDecimal price, String category, boolean veg, boolean available,
    OffsetDateTime createdAt, OffsetDateTime updatedAt
) {}
