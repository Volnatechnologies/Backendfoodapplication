package com.caloryhive.business.reviews.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReviewReplyRequest {

    @NotBlank(message = "Response text cannot be empty")
    @Size(max = 1000, message = "Response cannot exceed 1000 characters")
    private String responseText;
}
