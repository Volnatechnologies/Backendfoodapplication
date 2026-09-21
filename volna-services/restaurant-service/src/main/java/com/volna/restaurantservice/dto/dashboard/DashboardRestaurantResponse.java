package com.volna.restaurantservice.dto.dashboard;

import com.volna.restaurantservice.entity.RestaurantStatus;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

public record DashboardRestaurantResponse(
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
        RestaurantStatus status
) {
}