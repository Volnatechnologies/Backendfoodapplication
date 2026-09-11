package com.caloryhive.business.catering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventTypeResponse {
    private UUID id;
    private String name;
    private String description;
    private boolean active;
}
