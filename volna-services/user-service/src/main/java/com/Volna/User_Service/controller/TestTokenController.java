package com.Volna.User_Service.controller;
import com.Volna.User_Service.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/test")
@RequiredArgsConstructor
public class TestTokenController {

    private final JwtTokenProvider jwtTokenProvider;

    @PostMapping("/token")
    public ResponseEntity<String> generateToken(
            @RequestParam UUID userId) {

        String token = jwtTokenProvider.generateToken(userId);

        return ResponseEntity.ok(token);
    }
}