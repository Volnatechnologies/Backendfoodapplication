package com.volna.restaurantservice.dto.menu.request;

import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class VisibilityUpdateRequestDTO {
    @NotNull(message = "Visibility flag cannot be null")
    private Boolean isVisible;
}
