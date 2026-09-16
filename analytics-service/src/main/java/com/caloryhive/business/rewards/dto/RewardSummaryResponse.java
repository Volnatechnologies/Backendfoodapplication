package com.caloryhive.business.rewards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardSummaryResponse {
    private String currentTierName;
    private String currentTierDisplay;
    private String tierTagline;
    private Integer totalPointsEarned;
    private Integer currentPointsBalance;
    private String nextTierName;
    private Integer nextTierPointsThreshold;
    private Integer pointsToNextTier;
    private Double tierProgressPercent;
    private List<PerkResponse> currentPerks;
    private PerkResponse upcomingPerk;
}
