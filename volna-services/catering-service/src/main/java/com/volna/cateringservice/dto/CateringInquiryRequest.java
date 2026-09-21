package com.volna.cateringservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CateringInquiryRequest {

    @NotBlank
    private String name;

    @Email
    private String email;

    private String phone;
    private String eventType;
    private LocalDate eventDate;

    @Min(1)
    private Integer guestCount;

    private String message;
}
