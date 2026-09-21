package com.volna.menuservice.repository;
import com.volna.menuservice.entity.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface MenuItemRepository extends JpaRepository<MenuItem, UUID> {
    List<MenuItem> findByRestaurantIdOrderByNameAsc(UUID restaurantId);
    List<MenuItem> findByRestaurantIdAndAvailableTrueOrderByNameAsc(UUID restaurantId);
}
