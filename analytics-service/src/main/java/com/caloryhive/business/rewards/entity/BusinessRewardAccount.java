package com.caloryhive.business.rewards.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "business_reward_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessRewardAccount {

    @Id
    private UUID id;

    @Column(name = "business_id", nullable = false, unique = true)
    private UUID businessId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "current_tier_id", nullable = false)
    private RewardTier currentTier;

    @Column(name = "total_points_earned", nullable = false)
    @Builder.Default
    private Integer totalPointsEarned = 0;

    @Column(name = "current_points_balance", nullable = false)
    @Builder.Default
    private Integer currentPointsBalance = 0;

    @Version
    @Column(nullable = false)
    @Builder.Default
    private Long version = 0L;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private OffsetDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private OffsetDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID();
        }
    }
}
