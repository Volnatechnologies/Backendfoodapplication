package com.caloryhive.business.rewards;

import com.caloryhive.business.audit.service.AuditService;
import com.caloryhive.business.common.exception.InsufficientPointsException;
import com.caloryhive.business.rewards.dto.RedeemRewardRequest;
import com.caloryhive.business.rewards.entity.BusinessRewardAccount;
import com.caloryhive.business.rewards.entity.RewardCatalogItem;
import com.caloryhive.business.rewards.entity.RewardRedemption;
import com.caloryhive.business.rewards.entity.RewardTier;
import com.caloryhive.business.rewards.repository.*;
import com.caloryhive.business.rewards.service.impl.RewardsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RewardsConcurrencyTest {

    @Mock
    private BusinessRewardAccountRepository accountRepository;

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

    @BeforeEach
    void setUp() {
        businessId = UUID.randomUUID();
        userId = UUID.randomUUID();
        itemId = UUID.randomUUID();
    }

    @Test
    void testConcurrentRedemptionPessimisticWriteProtection() throws InterruptedException {
        // Business only has 5,000 points. Item costs 5,000 points.
        // 2 parallel threads attempt to redeem simultaneously.
        // Exactly 1 must succeed, and 1 must fail with InsufficientPointsException.
        RewardCatalogItem item = RewardCatalogItem.builder()
                .id(itemId)
                .title("Pro Photoshoot")
                .pointsCost(5000)
                .isActive(true)
                .build();

        BusinessRewardAccount account = BusinessRewardAccount.builder()
                .id(UUID.randomUUID())
                .businessId(businessId)
                .currentTier(RewardTier.builder().id(UUID.randomUUID()).tierName("PLATINUM").build())
                .totalPointsEarned(5000)
                .currentPointsBalance(5000)
                .build();

        when(catalogItemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(accountRepository.findByBusinessIdWithLock(businessId)).thenReturn(Optional.of(account));
        when(redemptionRepository.save(any(RewardRedemption.class))).thenAnswer(inv -> {
            RewardRedemption r = inv.getArgument(0);
            r.setId(UUID.randomUUID());
            r.setCreatedAt(OffsetDateTime.now());
            return r;
        });

        int numThreads = 2;
        ExecutorService executor = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(1);
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);

        for (int i = 0; i < numThreads; i++) {
            executor.submit(() -> {
                try {
                    latch.await();
                    synchronized (account) {
                        rewardsService.redeemReward(businessId, userId, new RedeemRewardRequest(itemId), "127.0.0.1", "JUnit");
                    }
                    successCount.incrementAndGet();
                } catch (InsufficientPointsException e) {
                    failCount.incrementAndGet();
                } catch (Exception e) {
                    failCount.incrementAndGet();
                }
            });
        }

        latch.countDown();
        executor.shutdown();
        boolean finished = executor.awaitTermination(5, TimeUnit.SECONDS);

        assertEquals(true, finished);
        assertEquals(1, successCount.get(), "Only 1 redemption should succeed");
        assertEquals(1, failCount.get(), "Second redemption must fail due to zero remaining points");
        assertEquals(0, account.getCurrentPointsBalance(), "Balance should never drop below zero");
    }
}
