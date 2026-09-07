package com.volna.restaurantservice.dto.menu.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComboRequestDTO {

    private UUID restaurantId;

    @NotBlank(message = "Combo title is required")
    private String title;

    private String description;

    @NotNull(message = "Combo price is required")
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    private BigDecimal comboPrice;

    @NotEmpty(message = "At least one item must be included in the combo")
    private Set<Long> itemIds;

    @Builder.Default
    private Boolean isActive = true;
}
