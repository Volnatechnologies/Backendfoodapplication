package com.volna.restaurantservice.dto.menu.request;

import com.volna.restaurantservice.entity.enums.StockStatus;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StatusUpdateRequest {
    @NotNull(message = "Status cannot be null")
    private StockStatus status;
}
