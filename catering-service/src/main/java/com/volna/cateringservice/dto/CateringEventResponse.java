package com.volna.cateringservice.dto;

import com.volna.cateringservice.entity.CateringEventStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Value
@Builder
public class CateringEventResponse {
    Long id;
    String bookingReference;
    String eventName;
    String eventType;
    LocalDate eventDate;
    LocalTime eventTime;
    Integer guestCount;
    String venueAddress;
    String specialInstructions;
    CateringEventStatus status;
    CateringPackageResponse cateringPackage;
    BigDecimal subtotal;
    BigDecimal customOptionsTotal;
    BigDecimal serviceFee;
    BigDecimal taxAmount;
    BigDecimal totalAmount;
    BigDecimal depositAmount;
    List<CustomOptionResponse> customOptions;
}
