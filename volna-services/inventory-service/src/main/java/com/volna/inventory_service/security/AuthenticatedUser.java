package com.volna.inventory_service.security;
import java.util.UUID;
public record AuthenticatedUser(UUID authUserId, String role) {}
