package com.caloryhive.business.analytics.repository;

import com.caloryhive.business.analytics.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, UUID> {

    @Query("SELECT oi.menuItemName, oi.categoryName, SUM(oi.quantity), SUM(oi.quantity * oi.price), MAX(oi.imageUrl) " +
           "FROM OrderItem oi JOIN oi.order o " +
           "WHERE o.businessId = :businessId " +
           "GROUP BY oi.menuItemName, oi.categoryName " +
           "ORDER BY SUM(oi.quantity) DESC")
    List<Object[]> findTopSellingItems(@Param("businessId") UUID businessId);
}
