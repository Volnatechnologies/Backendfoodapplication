package com.caloryhive.business.catering.dto;

import com.caloryhive.business.common.enums.BookingStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BookingListItemResponse {
    private UUID id;
    private String bookingCode;
    private String eventName;
    private String eventType;
    private LocalDate eventDate;
    private LocalTime eventTime;
    private Integer guestCount;
    private String venueAddress;
    private BigDecimal finalTotal;
    private BookingStatus status;
}


