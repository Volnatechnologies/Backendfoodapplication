package com.caloryhive.business.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CapitalEligibilityResponse {
    private Boolean eligible;
    private BigDecimal maxLoanAmount;
    private BigDecimal interestRatePercent;
    private Integer zeroInterestDays;
    private String qualificationBasis;
    private String termsSummary;
    private String learnMoreUrl;
}
