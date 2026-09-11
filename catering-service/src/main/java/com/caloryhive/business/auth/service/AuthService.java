package com.caloryhive.business.auth.service;

import com.caloryhive.business.auth.dto.AuthResponse;
import com.caloryhive.business.auth.dto.LoginRequest;
import com.caloryhive.business.auth.dto.RegisterRequest;
import com.caloryhive.business.common.enums.RoleName;
import com.caloryhive.business.common.exception.DuplicateResourceException;
import com.caloryhive.business.common.exception.ResourceNotFoundException;
import com.caloryhive.business.security.JwtService;
import com.caloryhive.business.user.entity.Role;
import com.caloryhive.business.user.entity.User;
import com.caloryhive.business.user.repository.RoleRepository;
import com.caloryhive.business.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail().trim().toLowerCase())) {
            throw new DuplicateResourceException("User with this email already exists");
        }

        Role userRole = roleRepository.findByName(RoleName.BUSINESS_OWNER)
                .orElseThrow(() -> new ResourceNotFoundException("Default role not found"));

        User user = new User();
        user.setFirstName(request.getFirstName().trim());
        user.setLastName(request.getLastName().trim());
        user.setEmail(request.getEmail().trim().toLowerCase());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setRoles(Set.of(userRole));
        user.setEnabled(true);

        userRepository.save(user);
        return buildAuthResponse(user);
    }

    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail().trim().toLowerCase(), request.getPassword())
        );

        if (!authentication.isAuthenticated()) {
            throw new ResourceNotFoundException("Authentication failed");
        }

        User user = userRepository.findByEmail(request.getEmail().trim().toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        return buildAuthResponse(user);
    }

    public AuthResponse.UserSummary getCurrentUser(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return AuthResponse.UserSummary.builder()
                .id(user.getId().toString())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .role(user.getRoles().stream().findFirst().map(role -> role.getName().name()).orElse("BUSINESS_OWNER"))
                .build();
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                user.getRoles().stream().map(role -> new org.springframework.security.core.authority.SimpleGrantedAuthority("ROLE_" + role.getName().name())).toList()
        ));

        return AuthResponse.builder()
                .token(token)
                .tokenType("Bearer")
                .expiresIn(86400000L)
                .user(AuthResponse.UserSummary.builder()
                        .id(user.getId().toString())
                        .email(user.getEmail())
                        .firstName(user.getFirstName())
                        .lastName(user.getLastName())
                        .role(user.getRoles().stream().findFirst().map(role -> role.getName().name()).orElse("BUSINESS_OWNER"))
                        .build())
                .build();
    }
}

