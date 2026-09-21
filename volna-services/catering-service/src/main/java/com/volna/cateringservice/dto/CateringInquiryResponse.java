package com.volna.cateringservice.dto;

import com.volna.cateringservice.entity.CateringInquiryStatus;
import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Value
@Builder
public class CateringInquiryResponse {
    Long id;
    String name;
    String email;
    String phone;
    String eventType;
    LocalDate eventDate;
    Integer guestCount;
    String message;
    CateringInquiryStatus status;
    LocalDateTime createdAt;
}
