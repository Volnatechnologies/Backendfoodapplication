package com.volna.restaurantservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(

        UUID customerId,

        @NotBlank(message = "Customer name is required")
        @Size(max = 150, message = "Customer name must not exceed 150 characters")
        String customerName,

        @Size(max = 30, message = "Customer phone must not exceed 30 characters")
        String customerPhone,

        @NotBlank(message = "Order type is required")
        @Size(max = 30, message = "Order type must not exceed 30 characters")
        String orderType,

        @Size(max = 500, message = "Delivery address must not exceed 500 characters")
        String deliveryAddress,

        @Size(max = 30, message = "Payment status must not exceed 30 characters")
        String paymentStatus,

        @NotEmpty(message = "At least one order item is required")
        List<@Valid OrderItemRequest> items
) {
}