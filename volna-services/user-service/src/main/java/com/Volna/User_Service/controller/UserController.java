package com.Volna.User_Service.controller;
import com.Volna.User_Service.dto.*;
import com.Volna.User_Service.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserProfileResponseDto> getProfile(
            @AuthenticationPrincipal String authUserId) {

        UUID userId = UUID.fromString(authUserId);

        return ResponseEntity.ok(
                userService.getProfile(userId)
        );
    }

    @PutMapping("/me")
    public ResponseEntity<UserProfileResponseDto> updateProfile(
            @AuthenticationPrincipal String authUserId,
            @Valid @RequestBody UserProfileRequestDto dto) {

        UUID userId = UUID.fromString(authUserId);

        return ResponseEntity.ok(
                userService.updateProfile(userId, dto)
        );
    }
}