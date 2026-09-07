package com.volna.restaurantservice.dto.menu.response;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MenuCategoryResponse {
    private Long id;
    private UUID restaurantId;
    private String name;
    private String description;
    private Boolean isActive;
    private Integer sortOrder;
    private Integer itemCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
