package com.caloryhive.business.catering.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateCustomOptionsRequest {
    @NotNull(message = "Custom option IDs list must not be null")
    @Builder.Default
    private List<UUID> customOptionIds = new ArrayList<>();
}
