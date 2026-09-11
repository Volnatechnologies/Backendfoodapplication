package com.caloryhive.business.catering.dto;

import com.caloryhive.business.common.enums.InquiryStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InquirySummaryResponse {
    private UUID id;
    private String title;
    private String clientName;
    private String messageSnippet;
    private String eventType;
    private String relativeTime;
    private InquiryStatus status;
    private LocalDateTime createdAt;
}
