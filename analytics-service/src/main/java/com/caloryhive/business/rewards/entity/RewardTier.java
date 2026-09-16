package com.caloryhive.business.rewards.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "reward_tiers")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RewardTier {

    @Id
    private UUID id;

    @Column(name = "tier_name", nullable = false, unique = true, length = 50)
    private String tierName;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "min_points", nullable = false)
    private Integer minPoints;

    @Column(name = "next_tier_name", length = 50)
    private String nextTierName;

    @Column(name = "next_tier_points")
    private Integer nextTierPoints;

    @Column(name = "commission_discount_percent", precision = 5, scale = 2)
    @Builder.Default
    private BigDecimal commissionDiscountPercent = BigDecimal.ZERO;

    @Column(length = 200)
    private String tagline;
}
