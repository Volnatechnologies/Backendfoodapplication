package com.Volna.User_Service.dto;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AddressRequestDto {

    @NotBlank(message = "Address type is required (e.g., HOME, WORK)")
    private String addressType;

    @NotBlank(message = "House number or flat details required")
    private String houseNumber;

    @NotBlank(message = "Street address is required")
    private String streetAddress;

    private String landmark;

    @NotBlank(message = "City is required")
    private String city;

    @NotBlank(message = "State is required")
    private String state;

    @NotBlank(message = "Pincode is required")
    private String pincode;

    private Double latitude;
    private Double longitude;

    @NotNull(message = "Default status must be specified")
    private Boolean isDefault;
}