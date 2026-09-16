package com.caloryhive.business.reviews.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewResponseDto {
    private UUID id;
    private String customerName;
    private String customerAvatar;
    private Integer rating;
    private String comment;
    private String sentiment;
    private BigDecimal sentimentScore;
    private Boolean answered;
    private Boolean hasPhotos;
    private List<String> photos;
    private String orderReference;
    private OffsetDateTime createdAt;
    private String formattedDate;
    private ReviewReplyResponse reply;
}
