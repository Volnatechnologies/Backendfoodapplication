package com.volna.customerorder.dto;
import java.math.BigDecimal;
import java.util.UUID;
public record CheckoutPreviewResponse(UUID restaurantId,UUID addressId,BigDecimal subtotal,BigDecimal tax,BigDecimal deliveryFee,BigDecimal packagingFee,BigDecimal discount,BigDecimal total){}
