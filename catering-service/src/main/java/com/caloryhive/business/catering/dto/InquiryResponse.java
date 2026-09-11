package com.caloryhive.business.catering.dto;

import com.caloryhive.business.common.enums.InquiryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquiryResponse {
    private UUID id;
    private String name;
    private String email;
    private String phone;
    private String eventType;
    private String message;
    private Integer guestCount;
    private LocalDate eventDate;
    private InquiryStatus status;
    private String relativeTime;
    private LocalDateTime readAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
