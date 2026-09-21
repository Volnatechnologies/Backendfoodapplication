package com.volna.restaurantservice.repository;
import com.volna.restaurantservice.entity.Restaurant; import org.springframework.data.jpa.repository.JpaRepository; import java.util.*;
public interface RestaurantRepository extends JpaRepository<Restaurant,UUID>{
 boolean existsByOwnerId(UUID ownerId);
 boolean existsByEmailIgnoreCase(String email);
 Optional<Restaurant> findByOwnerId(UUID ownerId);
}