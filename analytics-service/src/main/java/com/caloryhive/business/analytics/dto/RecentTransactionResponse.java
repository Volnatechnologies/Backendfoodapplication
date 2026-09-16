package com.caloryhive.business.analytics.dto;

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
public class RecentTransactionResponse {
    private UUID id;
    private String orderId;
    private String dateTimeFormatted;
    private OffsetDateTime createdAt;
    private String customerName;
    private String type;
    private BigDecimal amount;
    private String status;
    private String paymentStatus;
}
