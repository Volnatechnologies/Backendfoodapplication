package com.volna.authservice.controller;
import com.volna.authservice.dto.*;
import com.volna.authservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
@RestController @RequestMapping("/api/v1/auth") @RequiredArgsConstructor
public class AuthController {
 private final AuthService service;
 @PostMapping("/register")
 @ResponseStatus(HttpStatus.CREATED)
 public AuthResponse register(@Valid @RequestBody RegisterRequest r)
 {
  return service.register(r);
 }
 @PostMapping("/login")
 public AuthResponse login(@Valid @RequestBody LoginRequest r){
  return service.login(r);}
}