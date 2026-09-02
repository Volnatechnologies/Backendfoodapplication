package com.volna.restaurantservice.dto;

import com.volna.restaurantservice.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record OrderResponse(

        UUID id,

        UUID restaurantId,

        UUID customerId,

        String customerName,

        String customerPhone,

        String orderType,

        String deliveryAddress,

        BigDecimal totalAmount,

        OrderStatus status,

        String paymentStatus,

        String rejectionReason,

        OffsetDateTime createdAt,

        OffsetDateTime updatedAt,

        List<OrderItemResponse> items
) {

    public record OrderItemResponse(

            UUID id,

            String name,

            Integer quantity,

            BigDecimal price
    ) {
    }
}