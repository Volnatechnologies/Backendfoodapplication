package com.volna.restaurantservice.dto.menu.request;

import com.volna.restaurantservice.entity.enums.SpicyLevel;
import com.volna.restaurantservice.entity.enums.StockStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItemUpdateRequest {

    @NotBlank(message = "Item name is required")
    @Size(max = 150, message = "Item name cannot exceed 150 characters")
    private String name;

    private String description;
    private String internalCode;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "Discounted price cannot be negative")
    private BigDecimal discountedPrice;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @Min(value = 0, message = "Calories cannot be negative")
    private Integer calories;

    @PositiveOrZero(message = "Protein must be zero or positive")
    private BigDecimal protein;

    @PositiveOrZero(message = "Carbohydrates must be zero or positive")
    private BigDecimal carbohydrates;

    @PositiveOrZero(message = "Fats must be zero or positive")
    private BigDecimal fats;

    private Set<String> dietaryTags;
    private Set<String> allergens;
    private SpicyLevel spicyLevel;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    private Integer stockQuantity;

    private StockStatus status;
    private Boolean isVisible;
    private Boolean isAvailable;
    private String imageUrl;
}
