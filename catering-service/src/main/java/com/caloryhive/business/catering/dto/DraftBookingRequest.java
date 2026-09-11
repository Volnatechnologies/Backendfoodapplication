package com.caloryhive.business.catering.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
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
public class DraftBookingRequest {
    @NotBlank(message = "Event name is required to save a draft")
    @Size(max = 200, message = "Event name must be less than 200 characters")
    private String eventName;

    private UUID eventTypeId;

    private LocalDate eventDate;

    private LocalTime eventTime;

    @Positive(message = "Guest count must be greater than 0 if provided")
    private Integer guestCount;

    private String venueAddress;

    private String specialInstructions;

    private UUID menuPackageId;

    @Builder.Default
    private List<UUID> customOptionIds = new ArrayList<>();
}
