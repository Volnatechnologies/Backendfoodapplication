package com.caloryhive.business.finance.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateWithdrawalRequest {

    @NotNull(message = "Withdrawal amount is required")
    @DecimalMin(value = "100.00", message = "Minimum withdrawal amount is $100.00")
    private BigDecimal amount;

    @NotNull(message = "Bank account ID is required")
    private UUID bankAccountId;
}
