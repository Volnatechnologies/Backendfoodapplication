package com.volna.restaurantservice.service.impl;
import com.volna.restaurantservice.dto.*; import com.volna.restaurantservice.entity.*; import com.volna.restaurantservice.exception.*; import com.volna.restaurantservice.repository.RestaurantRepository; import com.volna.restaurantservice.service.RestaurantService; import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import java.time.LocalTime; import java.util.*;
@Service
@RequiredArgsConstructor
public class RestaurantServiceImpl implements RestaurantService {
 private final RestaurantRepository repo;
 @Transactional
 public RestaurantResponse create(UUID ownerId,RestaurantCreateRequest r){
  if(repo.existsByOwnerId(ownerId))
   throw new BadRequestException("Restaurant already exists for this owner");
  String email=r.email().trim().toLowerCase(Locale.ROOT);
  if(repo.existsByEmailIgnoreCase(email))
   throw new BadRequestException("Restaurant email is already registered");
  validateTime(r.openingTime(),r.closingTime());
  Restaurant x=Restaurant.builder()
          .id(UUID.randomUUID())
          .ownerId(ownerId)
          .name(r.name().trim())
          .description(r.description())
          .phone(r.phone().trim())
          .email(email)
          .address(r.address().trim())
          .latitude(r.latitude()).longitude(r.longitude()).openingTime(r.openingTime())
          .closingTime(r.closingTime()).status(RestaurantStatus.PENDING)
          .build();
  return response(repo.save(x));
 }
 @Transactional(readOnly=true)
 public RestaurantResponse getMine(UUID ownerId){
  return repo.findByOwnerId(ownerId)
          .map(this::response)
          .orElseThrow(()->new ResourceNotFoundException("Restaurant not found for current user"));
 }
 @Transactional(readOnly=true)
 public RestaurantResponse getById(UUID id){
  return repo.findById(id)
          .map(this::response)
          .orElseThrow(()->new ResourceNotFoundException("Restaurant not found"));}
 @Transactional public RestaurantResponse update(UUID ownerId,UUID id,RestaurantUpdateRequest r){
  Restaurant x=repo.findById(id)
          .orElseThrow(()->new ResourceNotFoundException("Restaurant not found"));
  if(!x.getOwnerId().equals(ownerId))
   throw new ResourceNotFoundException("Restaurant not found");
  String email=r.email().trim().toLowerCase(Locale.ROOT);
  if(!x.getEmail().equalsIgnoreCase(email)&&repo.existsByEmailIgnoreCase(email))
   throw new BadRequestException("Restaurant email is already registered");
  validateTime(r.openingTime(),r.closingTime());
  x.setName(r.name().trim());
  x.setDescription(r.description());
  x.setPhone(r.phone().trim());x.setEmail(email);
  x.setAddress(r.address().trim());
  x.setLatitude(r.latitude());
  x.setLongitude(r.longitude());
  x.setOpeningTime(r.openingTime());
  x.setClosingTime(r.closingTime());
  return response(repo.save(x));
 }
 private void validateTime(LocalTime a,LocalTime b){
  if(a!=null&&b!=null&&!a.isBefore(b))
   throw new BadRequestException("Opening time must be before closing time");}
 private RestaurantResponse response(Restaurant x){
  return new RestaurantResponse(
          x.getId(),x.getOwnerId(),x.getName(),x.getDescription()
          ,x.getPhone(),x.getEmail(),x.getAddress()
          ,x.getLatitude(),x.getLongitude()
          ,x.getOpeningTime(),x.getClosingTime()
          ,x.getStatus(),x.getCreatedAt()
          ,x.getUpdatedAt());
 }
}