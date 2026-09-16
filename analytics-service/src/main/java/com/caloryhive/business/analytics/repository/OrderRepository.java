package com.caloryhive.business.analytics.repository;

import com.caloryhive.business.analytics.entity.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface OrderRepository extends JpaRepository<Order, UUID> {

    Optional<Order> findByIdAndBusinessId(UUID id, UUID businessId);

    @Query("SELECT COUNT(o) FROM Order o WHERE o.businessId = :businessId AND o.createdAt >= :startDate AND o.createdAt <= :endDate")
    long countOrdersInPeriod(@Param("businessId") UUID businessId, @Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o WHERE o.businessId = :businessId AND o.status = 'COMPLETED' AND o.createdAt >= :startDate AND o.createdAt <= :endDate")
    BigDecimal sumRevenueInPeriod(@Param("businessId") UUID businessId, @Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT COALESCE(AVG(o.satisfactionRating), 4.8) FROM Order o WHERE o.businessId = :businessId AND o.satisfactionRating IS NOT NULL AND o.createdAt >= :startDate AND o.createdAt <= :endDate")
    Double avgSatisfactionInPeriod(@Param("businessId") UUID businessId, @Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT o.orderType, COUNT(o) FROM Order o WHERE o.businessId = :businessId AND o.createdAt >= :startDate AND o.createdAt <= :endDate GROUP BY o.orderType")
    List<Object[]> countOrdersByChannel(@Param("businessId") UUID businessId, @Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT o FROM Order o WHERE o.businessId = :businessId AND (:search IS NULL OR LOWER(o.orderNumber) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(o.customerName) LIKE LOWER(CONCAT('%', :search, '%'))) ORDER BY o.createdAt DESC")
    Page<Order> searchOrders(@Param("businessId") UUID businessId, @Param("search") String search, Pageable pageable);

    @Query("SELECT o FROM Order o WHERE o.businessId = :businessId AND o.createdAt >= :startDate AND o.createdAt <= :endDate ORDER BY o.createdAt ASC")
    List<Order> findOrdersForEarnings(@Param("businessId") UUID businessId, @Param("startDate") OffsetDateTime startDate, @Param("endDate") OffsetDateTime endDate);

    @Query("SELECT COALESCE(AVG(o.fulfillmentTimeMinutes), 22) FROM Order o WHERE o.businessId = :businessId AND o.fulfillmentTimeMinutes IS NOT NULL")
    Double avgFulfillmentTime(@Param("businessId") UUID businessId);
}
