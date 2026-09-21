package com.volna.customerorder.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record CartItemResponse(UUID restaurantId, UUID menuItemId, String name, BigDecimal unitPrice, int quantity,
                               BigDecimal total) {
}
