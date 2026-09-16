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
public class WithdrawalResponse {
    private UUID id;
    private UUID bankAccountId;
    private String bankName;
    private String maskedAccountNumber;
    private BigDecimal amount;
    private String status;
    private String transactionReference;
    private String idempotencyKey;
    private OffsetDateTime createdAt;
    private String formattedDate;
}
