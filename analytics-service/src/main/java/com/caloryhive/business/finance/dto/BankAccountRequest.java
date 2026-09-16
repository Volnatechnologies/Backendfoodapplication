package com.caloryhive.business.finance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BankAccountRequest {

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Account holder name is required")
    private String accountHolderName;

    @NotBlank(message = "Account number is required")
    @Pattern(regexp = "^[0-9]{8,18}$", message = "Invalid account number format")
    private String accountNumber;

    @NotBlank(message = "Routing number / IFSC is required")
    private String routingNumber;

    @Builder.Default
    private String accountType = "Checking Account";

    @Builder.Default
    private Boolean isDefault = false;
}
