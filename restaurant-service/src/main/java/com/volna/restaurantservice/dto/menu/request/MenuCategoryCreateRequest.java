package com.volna.restaurantservice.dto.menu.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCategoryCreateRequest {

    private UUID restaurantId;

    @NotBlank(message = "Category name is required")
    @Size(max = 100, message = "Category name cannot exceed 100 characters")
    private String name;

    private String description;

    @Builder.Default
    private Boolean isActive = true;

    @Builder.Default
    private Integer sortOrder = 0;
}
