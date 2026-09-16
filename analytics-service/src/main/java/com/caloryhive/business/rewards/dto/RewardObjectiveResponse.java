package com.caloryhive.business.rewards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardObjectiveResponse {
    private UUID id;
    private String title;
    private String description;
    private String iconName;
    private Integer pointsReward;
    private BigDecimal currentValue;
    private BigDecimal targetValue;
    private String unit;
    private Integer progressPercentage;
    private String progressLabel;
    private Boolean isCompleted;
}
