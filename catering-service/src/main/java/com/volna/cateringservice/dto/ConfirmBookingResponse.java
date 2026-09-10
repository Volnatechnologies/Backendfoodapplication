package com.volna.cateringservice.dto;

import com.volna.cateringservice.entity.CateringEventStatus;
import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

@Value
@Builder
public class ConfirmBookingResponse {
    Long id;
    String bookingReference;
    String eventName;
    LocalDate eventDate;
    LocalTime eventTime;
    BigDecimal totalAmount;
    BigDecimal depositAmount;
    CateringEventStatus status;
    String message;
}
