package com.volna.restaurantservice.repository;

import com.volna.restaurantservice.entity.DocumentStatus;
import com.volna.restaurantservice.entity.RestaurantDocument;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RestaurantDocumentRepository
        extends JpaRepository<RestaurantDocument, UUID> {

 List<RestaurantDocument> findAllByRestaurantId(UUID restaurantId);

 long countByRestaurantId(UUID restaurantId);

 long countByRestaurantIdAndStatus(
         UUID restaurantId,
         DocumentStatus status
 );
}