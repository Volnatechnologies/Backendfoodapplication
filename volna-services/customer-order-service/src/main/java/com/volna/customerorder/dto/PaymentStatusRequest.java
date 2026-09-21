package com.volna.customerorder.dto;
import jakarta.validation.constraints.NotBlank;
public record PaymentStatusRequest(@NotBlank String status,@NotBlank String providerReference){}
