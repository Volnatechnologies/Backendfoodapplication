package com.volna.restaurantservice.dto.menu.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HappyHourRuleResponseDTO {
    private Long id;
    private UUID restaurantId;
    private String title;
    private BigDecimal discountPercentage;
    private LocalTime startTime;
    private LocalTime endTime;
    private Boolean isActive;
}
