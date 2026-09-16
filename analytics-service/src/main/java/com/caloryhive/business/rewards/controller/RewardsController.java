package com.caloryhive.business.rewards.controller;

import com.caloryhive.business.common.ApiResponse;
import com.caloryhive.business.rewards.dto.*;
import com.caloryhive.business.rewards.service.RewardsService;
import com.caloryhive.business.security.UserPrincipal;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/business/rewards")
@RequiredArgsConstructor
@Tag(name = "Partnership Rewards", description = "Endpoints for loyalty tiers, perks, milestones, point exchange, and ledger history")
public class RewardsController {

    private final RewardsService rewardsService;

    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get rewards & loyalty summary", description = "Returns current tier, earned points, next tier threshold, progress, and active perks.")
    public ApiResponse<RewardSummaryResponse> getSummary(@AuthenticationPrincipal UserPrincipal principal) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(rewardsService.getSummary(businessId), "Rewards summary fetched successfully");
    }

    @GetMapping("/objectives")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get active partnership objectives", description = "Returns active challenges (Maintain 4.8 Rating, Volume Excellence) and current milestone progress.")
    public ApiResponse<List<RewardObjectiveResponse>> getObjectives(@AuthenticationPrincipal UserPrincipal principal) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(rewardsService.getObjectives(businessId), "Reward objectives fetched successfully");
    }

    @GetMapping("/catalog")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get reward exchange catalog items")
    public ApiResponse<List<RewardCatalogItemResponse>> getCatalog() {
        return ApiResponse.success(rewardsService.getCatalog(), "Reward catalog fetched successfully");
    }

    @PostMapping("/redeem")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER')")
    @Operation(summary = "Redeem reward from catalog", description = "Reduces points atomically using pessimistic write locking, records ledger transaction and creates redemption record.")
    public ApiResponse<RedemptionResponse> redeemReward(
            @AuthenticationPrincipal UserPrincipal principal,
            @Valid @RequestBody RedeemRewardRequest request,
            HttpServletRequest httpRequest
    ) {
        UUID businessId = resolveBusinessId(principal);
        UUID userId = principal != null ? principal.getId() : UUID.fromString("00000000-0000-0000-0000-000000000002");

        RedemptionResponse response = rewardsService.redeemReward(
                businessId, userId, request,
                httpRequest.getRemoteAddr(), httpRequest.getHeader("User-Agent")
        );
        return ApiResponse.success(response, "Reward redeemed successfully");
    }

    @GetMapping("/points-history")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get points ledger transaction history")
    public ApiResponse<Page<PointHistoryItemResponse>> getPointsHistory(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID businessId = resolveBusinessId(principal);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success(rewardsService.getPointsHistory(businessId, pageable), "Points history retrieved successfully");
    }

    @GetMapping("/redemptions")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER')")
    @Operation(summary = "Get redeemed rewards history")
    public ApiResponse<Page<RedemptionResponse>> getRedemptions(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        UUID businessId = resolveBusinessId(principal);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return ApiResponse.success(rewardsService.getRedemptions(businessId, pageable), "Redemptions retrieved successfully");
    }

    @GetMapping("/perks")
    @PreAuthorize("hasAnyRole('BUSINESS_OWNER', 'BUSINESS_ADMIN', 'MANAGER', 'STAFF')")
    @Operation(summary = "Get tier perks and upcoming benefits")
    public ApiResponse<List<PerkResponse>> getPerks(@AuthenticationPrincipal UserPrincipal principal) {
        UUID businessId = resolveBusinessId(principal);
        return ApiResponse.success(rewardsService.getPerks(businessId), "Perks retrieved successfully");
    }

    private UUID resolveBusinessId(UserPrincipal principal) {
        if (principal != null && principal.getBusinessId() != null) {
            return principal.getBusinessId();
        }
        return UUID.fromString("00000000-0000-0000-0000-000000000001");
    }
}
