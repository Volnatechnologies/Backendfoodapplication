package com.caloryhive.business.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialSummaryResponse {
    private BigDecimal availableForWithdrawal;
    private Double balanceGrowthPercent;
    private String verifiedBy;
    private BigDecimal pendingPayouts;
    private Long pendingOrdersCount;
    private Integer pendingDaysExpected;
    private BigDecimal monthlyGoal;
    private BigDecimal monthlyGoalAchieved;
    private Double monthlyProgressPercent;
    private String currency;
    private LocalDate nextPayoutDate;
}
