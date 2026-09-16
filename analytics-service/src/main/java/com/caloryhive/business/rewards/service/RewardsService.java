package com.caloryhive.business.rewards.service;

import com.caloryhive.business.rewards.dto.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

public interface RewardsService {

    RewardSummaryResponse getSummary(UUID businessId);

    List<RewardObjectiveResponse> getObjectives(UUID businessId);

    List<RewardCatalogItemResponse> getCatalog();

    RedemptionResponse redeemReward(UUID businessId, UUID userId, RedeemRewardRequest request, String ipAddress, String userAgent);

    Page<PointHistoryItemResponse> getPointsHistory(UUID businessId, Pageable pageable);

    Page<RedemptionResponse> getRedemptions(UUID businessId, Pageable pageable);

    List<PerkResponse> getPerks(UUID businessId);
}
