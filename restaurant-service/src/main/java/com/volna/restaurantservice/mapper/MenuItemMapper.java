package com.volna.restaurantservice.mapper;

import com.volna.restaurantservice.dto.menu.request.MenuItemCreateRequest;
import com.volna.restaurantservice.dto.menu.request.MenuItemUpdateRequest;
import com.volna.restaurantservice.dto.menu.response.MenuItemResponse;
import com.volna.restaurantservice.entity.MenuCategory;
import com.volna.restaurantservice.entity.MenuItem;
import com.volna.restaurantservice.entity.enums.SpicyLevel;
import org.springframework.stereotype.Component;

import java.util.HashSet;

@Component
public class MenuItemMapper {

    public MenuItemResponse toResponse(MenuItem item) {
        if (item == null) {
            return null;
        }
        return MenuItemResponse.builder()
                .id(item.getId())
                .restaurantId(item.getRestaurantId())
                .name(item.getName())
                .description(item.getDescription())
                .internalCode(item.getInternalCode())
                .price(item.getPrice())
                .discountedPrice(item.getDiscountedPrice())
                .categoryId(item.getCategory() != null ? item.getCategory().getId() : null)
                .categoryName(item.getCategory() != null ? item.getCategory().getName() : null)
                .imageUrl(item.getImageUrl())
                .calories(item.getCalories())
                .protein(item.getProtein())
                .carbohydrates(item.getCarbohydrates())
                .fats(item.getFats())
                .dietaryTags(item.getDietaryTags() != null ? new HashSet<>(item.getDietaryTags()) : new HashSet<>())
                .allergens(item.getAllergens() != null ? new HashSet<>(item.getAllergens()) : new HashSet<>())
                .stockQuantity(item.getStockQuantity())
                .status(item.getStatus())
                .spicyLevel(item.getSpicyLevel())
                .isVisible(item.getIsVisible())
                .isAvailable(item.getIsAvailable())
                .createdAt(item.getCreatedAt())
                .updatedAt(item.getUpdatedAt())
                .build();
    }

    public MenuItem toEntity(MenuItemCreateRequest request, MenuCategory category, String imageUrl) {
        if (request == null) {
            return null;
        }
        MenuItem item = MenuItem.builder()
                .restaurantId(request.getRestaurantId())
                .name(request.getName().trim())
                .description(request.getDescription())
                .internalCode(request.getInternalCode())
                .price(request.getPrice())
                .discountedPrice(request.getDiscountedPrice())
                .category(category)
                .imageUrl(imageUrl)
                .calories(request.getCalories())
                .protein(request.getProtein())
                .carbohydrates(request.getCarbohydrates())
                .fats(request.getFats())
                .dietaryTags(request.getDietaryTags() != null ? new HashSet<>(request.getDietaryTags()) : new HashSet<>())
                .allergens(request.getAllergens() != null ? new HashSet<>(request.getAllergens()) : new HashSet<>())
                .spicyLevel(request.getSpicyLevel() != null ? request.getSpicyLevel() : SpicyLevel.NONE)
                .stockQuantity(request.getStockQuantity() != null ? request.getStockQuantity() : 0)
                .isVisible(request.getIsVisible() != null ? request.getIsVisible() : true)
                .isAvailable(request.getIsAvailable() != null ? request.getIsAvailable() : true)
                .build();

        if (request.getStatus() != null) {
            item.setStatus(request.getStatus());
        }
        return item;
    }

    public void updateEntityFromDTO(MenuItemUpdateRequest dto, MenuItem item, MenuCategory category, String imageUrl) {
        if (dto == null || item == null) {
            return;
        }
        item.setName(dto.getName().trim());
        item.setDescription(dto.getDescription());
        item.setInternalCode(dto.getInternalCode());
        item.setPrice(dto.getPrice());
        item.setDiscountedPrice(dto.getDiscountedPrice());
        item.setCategory(category);
        item.setCalories(dto.getCalories());
        item.setProtein(dto.getProtein());
        item.setCarbohydrates(dto.getCarbohydrates());
        item.setFats(dto.getFats());

        if (imageUrl != null) {
            item.setImageUrl(imageUrl);
        }

        if (dto.getDietaryTags() != null) {
            item.setDietaryTags(new HashSet<>(dto.getDietaryTags()));
        }
        if (dto.getAllergens() != null) {
            item.setAllergens(new HashSet<>(dto.getAllergens()));
        }
        if (dto.getSpicyLevel() != null) {
            item.setSpicyLevel(dto.getSpicyLevel());
        }
        if (dto.getStockQuantity() != null) {
            item.setStockQuantity(dto.getStockQuantity());
        }
        if (dto.getIsVisible() != null) {
            item.setIsVisible(dto.getIsVisible());
        }
        if (dto.getIsAvailable() != null) {
            item.setIsAvailable(dto.getIsAvailable());
        }
        if (dto.getStatus() != null) {
            item.setStatus(dto.getStatus());
        }
    }
}
