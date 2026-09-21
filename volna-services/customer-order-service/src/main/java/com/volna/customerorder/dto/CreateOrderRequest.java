package com.volna.customerorder.dto;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;
public record CreateOrderRequest(@NotNull UUID addressId,String paymentMethod,String idempotencyKey){}
