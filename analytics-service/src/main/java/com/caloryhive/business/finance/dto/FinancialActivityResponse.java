package com.caloryhive.business.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FinancialActivityResponse {
    private UUID id;
    private String type;
    private BigDecimal amount;
    private String status;
    private String description;
    private String referenceId;
    private OffsetDateTime createdAt;
    private String formattedDate;
}
