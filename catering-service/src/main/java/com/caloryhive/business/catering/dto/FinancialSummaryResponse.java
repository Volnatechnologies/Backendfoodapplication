package com.caloryhive.business.catering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialSummaryResponse {
    private Integer guestCount;
    private BigDecimal basePrice;
    private BigDecimal customOptionsTotal;
    private BigDecimal subtotal;
    private BigDecimal serviceFeePercentage;
    private BigDecimal serviceFee;
    private BigDecimal taxPercentage;
    private BigDecimal tax;
    private BigDecimal finalTotal;
    private BigDecimal depositPercentage;
    private BigDecimal depositRequired;
}
