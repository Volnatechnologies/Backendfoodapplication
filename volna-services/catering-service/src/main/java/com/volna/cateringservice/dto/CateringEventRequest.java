package com.volna.cateringservice.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CateringEventRequest {

    @NotBlank
    private String eventName;

    @NotBlank
    private String eventType;

    @NotNull
    @FutureOrPresent
    private LocalDate eventDate;

    @NotNull
    private LocalTime eventTime;

    @NotNull
    @Min(1)
    private Integer guestCount;

    private String venueAddress;
    private String specialInstructions;

    @NotNull
    private Long packageId;

    @Valid
    private List<CustomOptionRequest> customOptions = new ArrayList<>();
}
