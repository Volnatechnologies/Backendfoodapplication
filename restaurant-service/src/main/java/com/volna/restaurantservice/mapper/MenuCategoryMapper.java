package com.volna.restaurantservice.mapper;

import com.volna.restaurantservice.dto.menu.request.MenuCategoryCreateRequest;
import com.volna.restaurantservice.dto.menu.request.MenuCategoryUpdateRequest;
import com.volna.restaurantservice.dto.menu.response.MenuCategoryResponse;
import com.volna.restaurantservice.entity.MenuCategory;
import org.springframework.stereotype.Component;

@Component
public class MenuCategoryMapper {

    public MenuCategoryResponse toResponse(MenuCategory category) {
        if (category == null) {
            return null;
        }
        return MenuCategoryResponse.builder()
                .id(category.getId())
                .restaurantId(category.getRestaurantId())
                .name(category.getName())
                .description(category.getDescription())
                .isActive(category.getIsActive())
                .sortOrder(category.getSortOrder())
                .itemCount(category.getMenuItems() != null ? category.getMenuItems().size() : 0)
                .createdAt(category.getCreatedAt())
                .updatedAt(category.getUpdatedAt())
                .build();
    }

    public MenuCategory toEntity(MenuCategoryCreateRequest request) {
        if (request == null) {
            return null;
        }
        return MenuCategory.builder()
                .restaurantId(request.getRestaurantId())
                .name(request.getName().trim())
                .description(request.getDescription())
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .sortOrder(request.getSortOrder() != null ? request.getSortOrder() : 0)
                .build();
    }

    public void updateEntityFromDTO(MenuCategoryUpdateRequest dto, MenuCategory entity) {
        if (dto == null || entity == null) {
            return;
        }
        if (dto.getName() != null) {
            entity.setName(dto.getName().trim());
        }
        if (dto.getDescription() != null) {
            entity.setDescription(dto.getDescription());
        }
        if (dto.getIsActive() != null) {
            entity.setIsActive(dto.getIsActive());
        }
        if (dto.getSortOrder() != null) {
            entity.setSortOrder(dto.getSortOrder());
        }
    }
}
