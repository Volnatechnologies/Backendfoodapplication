package com.caloryhive.business.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReplyResponse {
    private UUID id;
    private UUID reviewId;
    private String responderName;
    private String responseText;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;
}
