package com.caloryhive.business.finance.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountResponse {
    private UUID id;
    private String bankName;
    private String accountHolderName;
    private String accountType;
    private String maskedAccountNumber;
    private Boolean isDefault;
    private Boolean isVerified;
    private OffsetDateTime createdAt;
}
