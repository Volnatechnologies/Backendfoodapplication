package com.caloryhive.business.staff.dto;

import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReviewRequestDto {

    @Size(max = 500, message = "Admin comment cannot exceed 500 characters")
    private String adminComment;
}
