package com.volna.customerorder.dto;
import jakarta.validation.constraints.*;
import java.util.UUID;
public record CartItemRequest(@NotNull UUID restaurantId,@NotNull UUID menuItemId,@Min(1) int quantity){}
