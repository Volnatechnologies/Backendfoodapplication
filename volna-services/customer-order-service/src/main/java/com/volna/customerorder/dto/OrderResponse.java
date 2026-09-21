package com.volna.customerorder.dto;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;
public record OrderResponse(UUID id,UUID customerId,UUID restaurantId,UUID addressId,String status,BigDecimal subtotal,BigDecimal tax,BigDecimal deliveryFee,BigDecimal packagingFee,BigDecimal discount,BigDecimal totalAmount,String paymentStatus,List<OrderItemResponse> items,OffsetDateTime createdAt,OffsetDateTime updatedAt){}
