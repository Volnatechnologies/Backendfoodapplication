package com.caloryhive.business.rewards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RedemptionResponse {
    private UUID id;
    private UUID catalogItemId;
    private String itemTitle;
    private Integer pointsSpent;
    private Integer remainingPointsBalance;
    private String status;
    private OffsetDateTime redeemedAt;
}
