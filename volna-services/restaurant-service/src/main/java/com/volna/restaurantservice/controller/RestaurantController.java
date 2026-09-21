package com.volna.restaurantservice.controller;
import com.volna.restaurantservice.dto.*; import com.volna.restaurantservice.service.RestaurantService; import jakarta.validation.Valid; import lombok.RequiredArgsConstructor; import org.springframework.http.HttpStatus; import org.springframework.security.access.prepost.PreAuthorize; import org.springframework.security.core.Authentication; import org.springframework.web.bind.annotation.*; import java.util.UUID;
@RestController
@RequestMapping("/api/v1/restaurants")
@RequiredArgsConstructor
public class RestaurantController {
 private final RestaurantService service;
 @PostMapping
 @ResponseStatus(HttpStatus.CREATED)
 @PreAuthorize("hasRole('RESTAURANT_OWNER')")
 public RestaurantResponse create(@Valid @RequestBody RestaurantCreateRequest r,Authentication a){
  return service.create(id(a),r);
 }
 @GetMapping("/me") @PreAuthorize("hasRole('RESTAURANT_OWNER')")
 public RestaurantResponse mine(Authentication a){
  return service.getMine(id(a));
 }
 @GetMapping("/{restaurantId}") public RestaurantResponse get(@PathVariable UUID restaurantId){
  return service.getById(restaurantId);
 }
 @PutMapping("/{restaurantId}")
 @PreAuthorize("hasRole('RESTAURANT_OWNER')")
 public RestaurantResponse update(@PathVariable UUID restaurantId,@Valid @RequestBody RestaurantUpdateRequest r,Authentication a){
  return service.update(id(a),restaurantId,r);
 }
 private UUID id(Authentication a){
  return UUID.fromString(a.getName());}
}