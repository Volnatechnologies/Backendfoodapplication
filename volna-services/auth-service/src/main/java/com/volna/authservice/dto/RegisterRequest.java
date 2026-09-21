package com.volna.authservice.dto;
import jakarta.validation.constraints.*;
public record RegisterRequest(
 @NotBlank @Size(max=150)
 String fullName,
 @NotBlank @Email
 String email,
 @NotBlank @Pattern(regexp="^[0-9+()\\- ]{8,20}$")
 String phoneNumber,
 @NotBlank @Size(min=8,max=100)
 String password
) {}