package com.Volna.User_Service.dto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserProfileRequestDto {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @Email(message = "Invalid email address format")
    private String email;

    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number format")
    private String phoneNumber;

    private String avatarUrl;
}