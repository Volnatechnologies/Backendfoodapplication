package com.volna.cateringservice.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CustomOptionResponse {
    Long id;
    String name;
    BigDecimal amount;
}
