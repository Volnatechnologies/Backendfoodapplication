package com.volna.cateringservice.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class CustomOptionRequest {

    @NotBlank
    private String name;

    @NotNull
    @DecimalMin("0.00")
    private BigDecimal amount;
}
