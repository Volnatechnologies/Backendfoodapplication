package com.volna.restaurantservice.dto;

import com.volna.restaurantservice.entity.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record UpdateOrderStatusRequest(

        @NotNull(message = "Order status is required")
        OrderStatus status
) {
}