package com.Volna.User_Service.dto;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class UserProfileResponseDto {
    private UUID id;
    private String fullName;
    private String email;
    private String phoneNumber;
    private String avatarUrl;
}