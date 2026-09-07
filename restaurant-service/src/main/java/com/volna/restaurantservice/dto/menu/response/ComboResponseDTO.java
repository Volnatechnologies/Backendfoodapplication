package com.volna.restaurantservice.dto.menu.response;

import lombok.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ComboResponseDTO {
    private Long id;
    private UUID restaurantId;
    private String title;
    private String description;
    private BigDecimal comboPrice;
    private List<MenuItemResponse> items;
    private Boolean isActive;
}
