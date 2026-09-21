package com.volna.restaurantservice.dto;
import jakarta.validation.constraints.*; import java.math.BigDecimal; import java.time.LocalTime;
public record RestaurantUpdateRequest(
 @NotBlank @Size(max=150)
 String name,
 @Size(max=1000)
 String description,
 @NotBlank @Pattern(regexp="^[0-9+()\\- ]{8,20}$")
 String phone,
 @NotBlank @Email
 String email,
 @NotBlank @Size(max=500)
 String address,
 @DecimalMin("-90.0") @DecimalMax("90.0")
 BigDecimal latitude,
 @DecimalMin("-180.0") @DecimalMax("180.0")
 BigDecimal longitude,
 LocalTime openingTime,
 LocalTime closingTime
) {}