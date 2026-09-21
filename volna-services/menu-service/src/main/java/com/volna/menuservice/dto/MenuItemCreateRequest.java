package com.volna.menuservice.dto;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.UUID;
public record MenuItemCreateRequest(
    @NotNull UUID restaurantId,
    @NotBlank @Size(max=150) String name,
    @Size(max=1000) String description,
    @Size(max=1000) String imageUrl,
    @NotNull @DecimalMin("0.01") @Digits(integer=10,fraction=2) BigDecimal price,
    @Size(max=80) String category,
    boolean veg,
    boolean available
) {}
