package com.Volna.User_Service.service;
import com.Volna.User_Service.dto.UserProfileRequestDto;
import com.Volna.User_Service.dto.UserProfileResponseDto;

import java.util.UUID;

public interface UserService {

    UserProfileResponseDto getProfile(UUID authUserId);

    UserProfileResponseDto updateProfile(
            UUID authUserId,
            UserProfileRequestDto dto
    );
}