package com.caloryhive.business.catering.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceCalculationRequest {
    @NotNull(message = "Menu package ID is required")
    private UUID menuPackageId;

    @NotNull(message = "Guest count is required")
    @Positive(message = "Guest count must be greater than 0")
    private Integer guestCount;

    @Builder.Default
    private List<UUID> customOptionIds = new ArrayList<>();
}
