package com.caloryhive.business.catering.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateInquiryRequest {
    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    @Email(message = "Valid email is required")
    private String email;

    private String phone;

    private String eventType;

    @NotBlank(message = "Message or requirements are required")
    private String message;

    @Positive(message = "Guest count must be greater than 0")
    private Integer guestCount;

    @FutureOrPresent(message = "Event date must not be in the past")
    private LocalDate eventDate;
}
