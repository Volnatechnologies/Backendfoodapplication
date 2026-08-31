package com.volna.authservice.dto;
import java.util.UUID;
public record AuthResponse(
        String accessToken,
        String tokenType,
        long expiresIn,
        UUID userId,
        String role
)
{}