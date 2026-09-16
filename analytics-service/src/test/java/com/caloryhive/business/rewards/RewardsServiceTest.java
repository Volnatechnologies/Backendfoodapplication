package com.caloryhive.business.rewards;

import com.caloryhive.business.audit.service.AuditService;
import com.caloryhive.business.common.exception.BadRequestException;
import com.caloryhive.business.common.exception.InsufficientPointsException;
import com.caloryhive.business.rewards.dto.RedeemRewardRequest;
import com.caloryhive.business.rewards.dto.RedemptionResponse;
import com.caloryhive.business.rewards.dto.RewardSummaryResponse;
import com.caloryhive.business.rewards.entity.*;
import com.caloryhive.business.rewards.repository.*;
import com.caloryhive.business.rewards.service.impl.RewardsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardsServiceTest {

    @Mock
    private BusinessRewardAccountRepository accountRepository;

    @Mock
    private RewardTierRepository tierRepository;

    @Mock
    private RewardPerkRepository perkRepository;

    @Mock
    private RewardObjectiveRepository objectiveRepository;

    @Mock
    private BusinessObjectiveProgressRepository progressRepository;

    @Mock
    private RewardCatalogItemRepository catalogItemRepository;

    @Mock
    private RewardRedemptionRepository redemptionRepository;

    @Mock
    private RewardTransactionRepository transactionRepository;

    @Mock
    private AuditService auditService;

    @InjectMocks
    private RewardsServiceImpl rewardsService;

    private UUID businessId;
    private UUID userId;
    private UUID itemId;
    private RewardTier platinumTier;

    @BeforeEach
    void setUp() {
        businessId = UUID.randomUUID();
        userId = UUID.randomUUID();
        itemId = UUID.randomUUID();

        platinumTier = RewardTier.builder()
                .id(UUID.randomUUID())
                .tierName("PLATINUM")
                .displayName("Platinum Partner")
                .tagline("Top 5% of CaloryeHive Restaurants")
                .minPoints(10000)
                .nextTierName("Diamond")
                .nextTierPoints(15000)
                .build();
    }

    @Test
    void testGetSummary() {
        BusinessRewardAccount account = BusinessRewardAccount.builder()
                .id(UUID.randomUUID())
                .businessId(businessId)
                .currentTier(platinumTier)
                .totalPointsEarned(12450)
                .currentPointsBalance(12450)
                .build();

        when(accountRepository.findByBusinessId(businessId)).thenReturn(Optional.of(account));
        when(perkRepository.findByTierIdOrderBySortOrderAsc(platinumTier.getId())).thenReturn(Collections.emptyList());

        RewardSummaryResponse summary = rewardsService.getSummary(businessId);

        assertNotNull(summary);
        assertEquals("PLATINUM", summary.getCurrentTierName());
        assertEquals("Platinum Partner", summary.getCurrentTierDisplay());
        assertEquals(12450, summary.getTotalPointsEarned());
        assertEquals(12450, summary.getCurrentPointsBalance());
        assertEquals(2550, summary.getPointsToNextTier()); // 15000 - 12450
        assertEquals(83.0, summary.getTierProgressPercent());
    }

    @Test
    void testRedeemRewardSuccess() {
        RewardCatalogItem item = RewardCatalogItem.builder()
                .id(itemId)
                .title("Pro Photoshoot")
                .pointsCost(5000)
                .isActive(true)
                .build();

        BusinessRewardAccount account = BusinessRewardAccount.builder()
                .id(UUID.randomUUID())
                .businessId(businessId)
                .currentTier(platinumTier)
                .totalPointsEarned(12450)
                .currentPointsBalance(12450)
                .build();

        when(catalogItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(accountRepository.findByBusinessIdWithLock(businessId)).thenReturn(Optional.of(account));
        when(redemptionRepository.save(any(RewardRedemption.class))).thenAnswer(inv -> {
            RewardRedemption r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            r.setCreatedAt(OffsetDateTime.now());
            return r;
        });

        RedeemRewardRequest req = new RedeemRewardRequest(itemId);
        RedemptionResponse resp = rewardsService.redeemReward(businessId, userId, req, "127.0.0.1", "JUnit");

        assertNotNull(resp);
        assertEquals("Pro Photoshoot", resp.getItemTitle());
        assertEquals(5000, resp.getPointsSpent());
        assertEquals(7450, resp.getRemainingPointsBalance()); // 12450 - 5000
        assertEquals(7450, account.getCurrentPointsBalance());
        verify(transactionRepository).save(any(RewardTransaction.class));
        verify(auditService).record(eq(businessId), eq(userId), eq("REDEEM_REWARD"), any(), any(), any(), any(), any());
    }

    @Test
    void testRedeemRewardInsufficientPointsThrowsException() {
        RewardCatalogItem item = RewardCatalogItem.builder()
                .id(itemId)
                .title("Sponsored Listing")
                .pointsCost(8500)
                .isActive(true)
                .build();

        BusinessRewardAccount account = BusinessRewardAccount.builder()
                .id(UUID.randomUUID())
                .businessId(businessId)
                .currentTier(platinumTier)
                .totalPointsEarned(5000)
                .currentPointsBalance(5000) // Less than 8500
                .build();

        when(catalogItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(accountRepository.findByBusinessIdWithLock(businessId)).thenReturn(Optional.of(account));

        RedeemRewardRequest req = new RedeemRewardRequest(itemId);

        assertThrows(InsufficientPointsException.class, () ->
                rewardsService.redeemReward(businessId, userId, req, "127.0.0.1", "JUnit"));
    }

    @Test
    void testRedeemInactiveRewardThrowsException() {
        RewardCatalogItem item = RewardCatalogItem.builder()
                .id(itemId)
                .title("Inactive Promo")
                .pointsCost(2000)
                .isActive(false)
                .build();

        when(catalogItemRepository.findById(itemId)).thenReturn(Optional.of(item));

        RedeemRewardRequest req = new RedeemRewardRequest(itemId);

        assertThrows(BadRequestException.class, () ->
                rewardsService.redeemReward(businessId, userId, req, "127.0.0.1", "JUnit"));
    }
}
