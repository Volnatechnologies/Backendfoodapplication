package com.volna.authservice.service;
import com.volna.authservice.dto.*;
import com.volna.authservice.entity.*;
import com.volna.authservice.repository.AuthUserRepository;
import com.volna.authservice.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.*;
@Service @RequiredArgsConstructor
public class AuthService {
 private final AuthUserRepository repository;
 private final PasswordEncoder encoder;
 private final JwtTokenProvider jwt;
 @Transactional public AuthResponse register(RegisterRequest r){
  String email=r.email().trim().toLowerCase(Locale.ROOT), phone=r.phoneNumber().trim();
  if(repository.existsByEmailIgnoreCase(email))
   throw new IllegalArgumentException("Email is already registered");
  if(repository.existsByPhoneNumber(phone))
   throw new IllegalArgumentException("Phone number is already registered");
  AuthUser u=AuthUser.builder().
          id(UUID.randomUUID()).
          fullName(r.fullName().
                  trim()).email(email).
          phoneNumber(phone)
   .passwordHash(encoder.encode(r.password())).role(Role.RESTAURANT_OWNER).enabled(true).build();
  return response(repository.save(u));
 }
 @Transactional(readOnly=true)
 public AuthResponse login(LoginRequest r){
  AuthUser u=repository.findByEmailIgnoreCase(r.email().trim())
          .orElseThrow(()->new IllegalArgumentException("Invalid email or password"));
  if(!u.isEnabled()||!encoder.matches(r.password(),u.getPasswordHash()))
   throw new IllegalArgumentException("Invalid email or password");
  return response(u);
 }
 private AuthResponse response(AuthUser u){
  return new AuthResponse(
          jwt.generateToken(u),"Bearer",
          jwt.getExpirationMs()/1000,
          u.getId(),
          u.getRole().name());
 }
}