package com.volna.restaurantservice.dto.menu.response;

import com.volna.restaurantservice.entity.enums.SpicyLevel;
import com.volna.restaurantservice.entity.enums.StockStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemResponse {
    private Long id;
    private UUID restaurantId;
    private String name;
    private String description;
    private String internalCode;
    private BigDecimal price;
    private BigDecimal discountedPrice;
    private Long categoryId;
    private String categoryName;
    private String imageUrl;
    private Integer calories;
    private BigDecimal protein;
    private BigDecimal carbohydrates;
    private BigDecimal fats;
    private Set<String> dietaryTags;
    private Set<String> allergens;
    private Integer stockQuantity;
    private StockStatus status;
    private SpicyLevel spicyLevel;
    private Boolean isVisible;
    private Boolean isAvailable;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
