package com.caloryhive.business.catering.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateBookingRequest {
    @NotBlank(message = "Event name is required")
    @Size(max = 200, message = "Event name must be less than 200 characters")
    private String eventName;

    @NotNull(message = "Event type is required")
    private UUID eventTypeId;

    @NotNull(message = "Event date is required")
    @FutureOrPresent(message = "Event date must not be in the past")
    private LocalDate eventDate;

    @NotNull(message = "Event time is required")
    private LocalTime eventTime;

    @NotNull(message = "Guest count is required")
    @Positive(message = "Guest count must be greater than 0")
    private Integer guestCount;

    @NotBlank(message = "Venue address is required")
    private String venueAddress;

    private String specialInstructions;

    @NotNull(message = "Menu package is required")
    private UUID menuPackageId;

    @Builder.Default
    private List<UUID> customOptionIds = new java.util.ArrayList<>();
}
