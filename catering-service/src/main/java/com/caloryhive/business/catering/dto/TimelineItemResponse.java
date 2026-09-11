package com.caloryhive.business.catering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimelineItemResponse {
    private String title;
    private String description;
    private String status;
    private LocalDateTime scheduledAt;
    private LocalDateTime completedAt;
}

