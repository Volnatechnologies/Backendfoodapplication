package com.volna.restaurantservice.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record RejectOrderRequest(

        @NotBlank(message = "Rejection reason is required")
        @Size(
                max = 500,
                message = "Rejection reason must not exceed 500 characters"
        )
        String reason
) {
}