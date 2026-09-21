package com.volna.restaurantservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record OrderItemRequest(

        @NotBlank(message = "Item name is required")
        @Size(max = 200, message = "Item name must not exceed 200 characters")
        String name,

        @NotNull(message = "Quantity is required")
        @Min(value = 1, message = "Quantity must be at least 1")
        Integer quantity,

        @NotNull(message = "Price is required")
        @DecimalMin(value = "0.00", message = "Price cannot be negative")
        BigDecimal price
) {
}