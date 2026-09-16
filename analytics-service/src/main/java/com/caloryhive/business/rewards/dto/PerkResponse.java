package com.caloryhive.business.rewards.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PerkResponse {
    private UUID id;
    private String title;
    private String description;
    private String iconName;
    private Boolean isUpcoming;
}
