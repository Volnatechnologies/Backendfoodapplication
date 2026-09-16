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
public class PointHistoryItemResponse {
    private UUID id;
    private String type; // EARN, REDEEM, ADJUSTMENT, BONUS
    private Integer points;
    private Integer balanceAfter;
    private String description;
    private String referenceType;
    private String referenceId;
    private OffsetDateTime createdAt;
    private String formattedDate;
}
