package com.volna.cateringservice.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;

@Value
@Builder
public class CateringPackageResponse {
    Long id;
    String name;
    String description;
    BigDecimal pricePerGuest;
    Integer maxGuests;
    Boolean active;
}
