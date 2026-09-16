package com.caloryhive.business.rewards.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedeemRewardRequest {

    @NotNull(message = "Catalog item ID is required")
    private UUID catalogItemId;
}
