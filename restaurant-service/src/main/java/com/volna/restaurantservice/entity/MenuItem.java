package com.volna.restaurantservice.entity;

import com.volna.restaurantservice.entity.enums.SpicyLevel;
import com.volna.restaurantservice.entity.enums.StockStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(
    name = "menu_items",
    indexes = {
        @Index(name = "idx_menu_item_name", columnList = "name"),
        @Index(name = "idx_menu_item_category_id", columnList = "category_id"),
        @Index(name = "idx_menu_item_status", columnList = "status"),
        @Index(name = "idx_menu_item_is_visible", columnList = "is_visible")
    }
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "restaurant_id")
    private UUID restaurantId;

    @NotBlank(message = "Menu item name cannot be blank")
    @Size(max = 150, message = "Menu item name cannot exceed 150 characters")
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "internal_code", length = 50)
    private String internalCode;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", message = "Price cannot be negative")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    @DecimalMin(value = "0.00", message = "Discounted price cannot be negative")
    @Column(name = "discounted_price", precision = 10, scale = 2)
    private BigDecimal discountedPrice;

    @NotNull(message = "Category is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_menu_item_category"))
    private MenuCategory category;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    @Min(value = 0, message = "Calories cannot be negative")
    @Column(name = "calories")
    private Integer calories;

    @PositiveOrZero(message = "Protein must be zero or positive")
    @Column(name = "protein", precision = 6, scale = 2)
    private BigDecimal protein;

    @PositiveOrZero(message = "Carbohydrates must be zero or positive")
    @Column(name = "carbohydrates", precision = 6, scale = 2)
    private BigDecimal carbohydrates;

    @PositiveOrZero(message = "Fats must be zero or positive")
    @Column(name = "fats", precision = 6, scale = 2)
    private BigDecimal fats;

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "menu_item_dietary_tags",
        joinColumns = @JoinColumn(name = "menu_item_id"),
        foreignKey = @ForeignKey(name = "fk_dietary_tags_menu_item")
    )
    @Column(name = "dietary_tag", length = 50)
    @Builder.Default
    private Set<String> dietaryTags = new HashSet<>();

    @ElementCollection(fetch = FetchType.LAZY)
    @CollectionTable(
        name = "menu_item_allergens",
        joinColumns = @JoinColumn(name = "menu_item_id"),
        foreignKey = @ForeignKey(name = "fk_allergens_menu_item")
    )
    @Column(name = "allergen", length = 50)
    @Builder.Default
    private Set<String> allergens = new HashSet<>();

    @Enumerated(EnumType.STRING)
    @Column(name = "spicy_level", nullable = false, length = 20)
    @Builder.Default
    private SpicyLevel spicyLevel = SpicyLevel.NONE;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity cannot be negative")
    @Column(name = "stock_quantity", nullable = false)
    @Builder.Default
    private Integer stockQuantity = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private StockStatus status = StockStatus.IN_STOCK;

    @Column(name = "is_visible", nullable = false)
    @Builder.Default
    private Boolean isVisible = true;

    @Column(name = "is_available", nullable = false)
    @Builder.Default
    private Boolean isAvailable = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    @PreUpdate
    public void calculateAvailabilityAndStatus() {
        if (this.stockQuantity == null || this.stockQuantity <= 0) {
            this.status = StockStatus.OUT_OF_STOCK;
            this.isAvailable = false;
        } else if (this.stockQuantity <= 10) {
            this.status = StockStatus.LOW_STOCK;
            this.isAvailable = true;
        } else {
            this.status = StockStatus.IN_STOCK;
            this.isAvailable = true;
        }
    }
}
