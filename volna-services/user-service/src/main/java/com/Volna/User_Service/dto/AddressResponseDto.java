package com.Volna.User_Service.dto;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class AddressResponseDto {
    private UUID id;
    private String addressType;
    private String houseNumber;
    private String streetAddress;
    private String landmark;
    private String city;
    private String state;
    private String pincode;
    private Double latitude;
    private Double longitude;
    private Boolean isDefault;
}