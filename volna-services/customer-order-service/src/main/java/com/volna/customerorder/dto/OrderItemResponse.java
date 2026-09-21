package com.volna.customerorder.dto;
import java.math.BigDecimal;
import java.util.UUID;
public record OrderItemResponse(UUID menuItemId,String itemNameSnapshot,BigDecimal unitPrice,int quantity,BigDecimal total){}
