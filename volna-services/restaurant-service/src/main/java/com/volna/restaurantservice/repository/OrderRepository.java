package com.volna.restaurantservice.repository;

import com.volna.restaurantservice.entity.Order;
import com.volna.restaurantservice.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface OrderRepository extends JpaRepository<Order, UUID> {

    List<Order> findByRestaurantIdOrderByCreatedAtDesc(
            UUID restaurantId
    );

    List<Order> findByRestaurantIdAndStatusInOrderByCreatedAtDesc(
            UUID restaurantId,
            List<OrderStatus> statuses
    );

    long countByRestaurantId(
            UUID restaurantId
    );

    long countByRestaurantIdAndStatus(
            UUID restaurantId,
            OrderStatus status
    );

    long countByRestaurantIdAndCreatedAtGreaterThanEqual(
            UUID restaurantId,
            OffsetDateTime startOfDay
    );

    long countByRestaurantIdAndStatusIn(
            UUID restaurantId,
            List<OrderStatus> statuses
    );
}