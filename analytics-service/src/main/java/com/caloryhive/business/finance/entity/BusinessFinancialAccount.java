package com.caloryhive.business.finance.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "business_financial_accounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BusinessFinancialAccount {

    @Id
    private UUID id;

    @Column(name = "business_id", nullable = false, unique = true)
    private UUID businessId;

    @Column(name = "available_balance", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal availableBalance = BigDecimal.ZERO;

    @Column(name = "pending_balance", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal pendingBalance = BigDecimal.ZERO;

    @Column(name = "monthly_goal", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal monthlyGoal = new BigDecimal("42500.00");

    @Column(name = "monthly_goal_achieved", nullable = false, precision = 14, scale = 2)
    @Builder.Default
    private BigDecimal monthlyGoalAchieved = new BigDecimal("27625.00");

    @Column(nullable = false, length = 10)
    @Builder.Default
    private String currency = "USD";

    @Column(name = "next_payout_date")
    private LocalDate nextPayoutDate;

    @Column(name = "verified_by", length = 100)
    @Builder.Default
    private String verifiedBy = "Partner Central Finance";

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
