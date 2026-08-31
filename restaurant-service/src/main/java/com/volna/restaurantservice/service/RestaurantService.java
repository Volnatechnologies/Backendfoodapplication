package com.volna.restaurantservice.service;
import com.volna.restaurantservice.dto.*; import java.util.UUID;
public interface RestaurantService {
 RestaurantResponse create(UUID ownerId,RestaurantCreateRequest r);
 RestaurantResponse getMine(UUID ownerId);
 RestaurantResponse getById(UUID id);
 RestaurantResponse update(UUID ownerId,UUID id,RestaurantUpdateRequest r);
}