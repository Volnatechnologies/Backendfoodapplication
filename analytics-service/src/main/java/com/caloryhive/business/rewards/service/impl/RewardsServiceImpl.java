package com.caloryhive.business.rewards.service.impl;

import com.caloryhive.business.audit.service.AuditService;
import com.caloryhive.business.common.exception.BadRequestException;
import com.caloryhive.business.common.exception.InsufficientPointsException;
import com.caloryhive.business.common.exception.ResourceNotFoundException;
import com.caloryhive.business.rewards.dto.*;
import com.caloryhive.business.rewards.entity.*;
import com.caloryhive.business.rewards.repository.*;
import com.caloryhive.business.rewards.service.RewardsService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.RoundingMode;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RewardsServiceImpl implements RewardsService {

    private static final Logger log = LoggerFactory.getLogger(RewardsServiceImpl.class);
    private static final DateTimeFormatter HISTORY_DATE_FORMAT = DateTimeFormatter.ofPattern("MMM dd, yyyy - hh:mm a");

    private final BusinessRewardAccountRepository accountRepository;
    private final RewardTierRepository tierRepository;
    private final RewardPerkRepository perkRepository;
    private final RewardObjectiveRepository objectiveRepository;
    private final BusinessObjectiveProgressRepository progressRepository;
    private final RewardCatalogItemRepository catalogItemRepository;
    private final RewardRedemptionRepository redemptionRepository;
    private final RewardTransactionRepository transactionRepository;
    private final AuditService auditService;

    @Override
    @Transactional(readOnly = true)
    public RewardSummaryResponse getSummary(UUID businessId) {
        BusinessRewardAccount account = accountRepository.findByBusinessId(businessId)
                .orElseGet(() -> createDefaultAccount(businessId));

        RewardTier tier = account.getCurrentTier();
        List<RewardPerk> perks = perkRepository.findByTierIdOrderBySortOrderAsc(tier.getId());

        List<PerkResponse> currentPerks = new ArrayList<>();
        PerkResponse upcomingPerk = null;

        for (RewardPerk p : perks) {
            PerkResponse dto = PerkResponse.builder()
                    .id(p.getId())
                    .title(p.getTitle())
                    .description(p.getDescription())
                    .iconName(p.getIconName())
                    .isUpcoming(p.getIsUpcoming())
                    .build();

            if (Boolean.TRUE.equals(p.getIsUpcoming())) {
                upcomingPerk = dto;
            } else {
                currentPerks.add(dto);
            }
        }

        int nextThreshold = tier.getNextTierPoints() != null ? tier.getNextTierPoints() : 15000;
        int pointsToNext = Math.max(0, nextThreshold - account.getCurrentPointsBalance());
        double progressPercent = nextThreshold > 0
                ? Math.min(100.0, Math.round(((double) account.getCurrentPointsBalance() / nextThreshold * 100.0) * 10.0) / 10.0)
                : 100.0;

        return RewardSummaryResponse.builder()
                .currentTierName(tier.getTierName())
                .currentTierDisplay(tier.getDisplayName())
                .tierTagline(tier.getTagline() != null ? tier.getTagline() : "Top 5% of CaloryeHive Restaurants")
                .totalPointsEarned(account.getTotalPointsEarned())
                .currentPointsBalance(account.getCurrentPointsBalance())
                .nextTierName(tier.getNextTierName() != null ? tier.getNextTierName() : "Diamond")
                .nextTierPointsThreshold(nextThreshold)
                .pointsToNextTier(pointsToNext)
                .tierProgressPercent(progressPercent)
                .currentPerks(currentPerks)
                .upcomingPerk(upcomingPerk)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RewardObjectiveResponse> getObjectives(UUID businessId) {
        List<RewardObjective> objectives = objectiveRepository.findAllByOrderBySortOrderAsc();
        List<BusinessObjectiveProgress> progresses = progressRepository.findByBusinessId(businessId);

        List<RewardObjectiveResponse> result = new ArrayList<>();
        for (RewardObjective obj : objectives) {
            Optional<BusinessObjectiveProgress> progOpt = progresses.stream()
                    .filter(p -> p.getObjective().getId().equals(obj.getId()))
                    .findFirst();

            BusinessObjectiveProgress prog = progOpt.orElse(null);

            result.add(RewardObjectiveResponse.builder()
                    .id(obj.getId())
                    .title(obj.getTitle())
                    .description(obj.getDescription())
                    .iconName(obj.getIconName())
                    .pointsReward(obj.getPointsReward())
                    .currentValue(prog != null ? prog.getCurrentValue() : obj.getTargetValue().multiply(new java.math.BigDecimal("0.66")))
                    .targetValue(obj.getTargetValue())
                    .unit(obj.getUnit())
                    .progressPercentage(prog != null ? prog.getProgressPercentage() : 66)
                    .progressLabel(prog != null ? prog.getProgressLabel() : "In Progress")
                    .isCompleted(prog != null ? prog.getIsCompleted() : false)
                    .build());
        }

        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public List<RewardCatalogItemResponse> getCatalog() {
        return catalogItemRepository.findByIsActiveTrueOrderByPointsCostAsc().stream()
                .map(item -> RewardCatalogItemResponse.builder()
                        .id(item.getId())
                        .title(item.getTitle())
                        .description(item.getDescription())
                        .pointsCost(item.getPointsCost())
                        .category(item.getCategory())
                        .imageUrl(item.getImageUrl())
                        .isActive(item.getIsActive())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public RedemptionResponse redeemReward(UUID businessId, UUID userId, RedeemRewardRequest request, String ipAddress, String userAgent) {
        // 1. Fetch item
        RewardCatalogItem catalogItem = catalogItemRepository.findById(request.getCatalogItemId())
                .orElseThrow(() -> new ResourceNotFoundException("Reward catalog item not found with id: " + request.getCatalogItemId()));

        if (!Boolean.TRUE.equals(catalogItem.getIsActive())) {
            throw new BadRequestException("This reward is currently not available for redemption.");
        }

        // 2. Lock reward account pessimistically to guarantee atomicity and concurrency safety
        BusinessRewardAccount account = accountRepository.findByBusinessIdWithLock(businessId)
                .orElseThrow(() -> new ResourceNotFoundException("Reward account not found for business"));

        // 3. Balance verification
        if (account.getCurrentPointsBalance() < catalogItem.getPointsCost()) {
            throw new InsufficientPointsException("Insufficient reward points. Item cost: " +
                    catalogItem.getPointsCost() + " pts, Available balance: " + account.getCurrentPointsBalance() + " pts");
        }

        // 4. Atomic points deduction
        int balanceAfter = account.getCurrentPointsBalance() - catalogItem.getPointsCost();
        account.setCurrentPointsBalance(balanceAfter);
        accountRepository.save(account);

        // 5. Create immutable redemption record
        RewardRedemption redemption = RewardRedemption.builder()
                .businessId(businessId)
                .catalogItem(catalogItem)
                .pointsSpent(catalogItem.getPointsCost())
                .status("COMPLETED")
                .build();
        RewardRedemption savedRedemption = redemptionRepository.save(redemption);

        // 6. Record transaction in ledger
        RewardTransaction tx = RewardTransaction.builder()
                .businessId(businessId)
                .type("REDEEM")
                .points(-catalogItem.getPointsCost())
                .referenceType("REWARD_REDEMPTION")
                .referenceId(savedRedemption.getId().toString())
                .balanceAfter(balanceAfter)
                .description("Redeemed: " + catalogItem.getTitle())
                .build();
        transactionRepository.save(tx);

        // 7. Record audit log
        auditService.record(businessId, userId, "REDEEM_REWARD", "RewardRedemption", savedRedemption.getId().toString(),
                "Redeemed reward '" + catalogItem.getTitle() + "' for " + catalogItem.getPointsCost() + " pts", ipAddress, userAgent);

        return RedemptionResponse.builder()
                .id(savedRedemption.getId())
                .catalogItemId(catalogItem.getId())
                .itemTitle(catalogItem.getTitle())
                .pointsSpent(catalogItem.getPointsCost())
                .remainingPointsBalance(balanceAfter)
                .status(savedRedemption.getStatus())
                .redeemedAt(savedRedemption.getCreatedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PointHistoryItemResponse> getPointsHistory(UUID businessId, Pageable pageable) {
        return transactionRepository.findByBusinessIdOrderByCreatedAtDesc(businessId, pageable)
                .map(tx -> PointHistoryItemResponse.builder()
                        .id(tx.getId())
                        .type(tx.getType())
                        .points(tx.getPoints())
                        .balanceAfter(tx.getBalanceAfter())
                        .description(tx.getDescription())
                        .referenceType(tx.getReferenceType())
                        .referenceId(tx.getReferenceId())
                        .createdAt(tx.getCreatedAt())
                        .formattedDate(tx.getCreatedAt() != null ? tx.getCreatedAt().format(HISTORY_DATE_FORMAT) : "")
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RedemptionResponse> getRedemptions(UUID businessId, Pageable pageable) {
        return redemptionRepository.findByBusinessIdOrderByCreatedAtDesc(businessId, pageable)
                .map(r -> RedemptionResponse.builder()
                        .id(r.getId())
                        .catalogItemId(r.getCatalogItem() != null ? r.getCatalogItem().getId() : null)
                        .itemTitle(r.getCatalogItem() != null ? r.getCatalogItem().getTitle() : "Reward Item")
                        .pointsSpent(r.getPointsSpent())
                        .status(r.getStatus())
                        .redeemedAt(r.getCreatedAt())
                        .build());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PerkResponse> getPerks(UUID businessId) {
        BusinessRewardAccount account = accountRepository.findByBusinessId(businessId)
                .orElseGet(() -> createDefaultAccount(businessId));

        return perkRepository.findByTierIdOrderBySortOrderAsc(account.getCurrentTier().getId()).stream()
                .map(p -> PerkResponse.builder()
                        .id(p.getId())
                        .title(p.getTitle())
                        .description(p.getDescription())
                        .iconName(p.getIconName())
                        .isUpcoming(p.getIsUpcoming())
                        .build())
                .toList();
    }

    private BusinessRewardAccount createDefaultAccount(UUID businessId) {
        RewardTier platinum = tierRepository.findByTierName("PLATINUM")
                .orElseGet(() -> tierRepository.findAll().stream().findFirst().orElseThrow());

        return BusinessRewardAccount.builder()
                .businessId(businessId)
                .currentTier(platinum)
                .totalPointsEarned(12450)
                .currentPointsBalance(12450)
                .build();
    }
}
