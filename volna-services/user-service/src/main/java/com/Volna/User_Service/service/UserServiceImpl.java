package com.Volna.User_Service.service;
import com.Volna.User_Service.dto.UserProfileRequestDto;
import com.Volna.User_Service.dto.UserProfileResponseDto;
import com.Volna.User_Service.entity.UserProfile;
import com.Volna.User_Service.exception.ResourceNotFoundException;
import com.Volna.User_Service.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserProfileRepository userProfileRepository;

    @Override
    @Transactional(readOnly = true)
    public UserProfileResponseDto getProfile(UUID authUserId) {

        UserProfile user = userProfileRepository
                .findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "User profile not found"
                        )
                );

        return mapToProfileDto(user);
    }

    @Override
    @Transactional
    public UserProfileResponseDto updateProfile(
            UUID authUserId,
            UserProfileRequestDto dto) {

        UserProfile user = userProfileRepository
                .findByAuthUserId(authUserId)
                .orElseGet(() ->
                        UserProfile.builder()
                                .authUserId(authUserId)
                                .build()
                );

        user.setFullName(dto.getFullName());
        user.setEmail(dto.getEmail());
        user.setPhoneNumber(dto.getPhoneNumber());
        user.setAvatarUrl(dto.getAvatarUrl());

        UserProfile saved =
                userProfileRepository.save(user);

        return mapToProfileDto(saved);
    }

    private UserProfileResponseDto mapToProfileDto(
            UserProfile user) {

        return UserProfileResponseDto.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .avatarUrl(user.getAvatarUrl())
                .build();
    }
}