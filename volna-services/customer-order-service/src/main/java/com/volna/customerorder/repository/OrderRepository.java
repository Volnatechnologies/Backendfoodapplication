package com.volna.customerorder.repository;
import com.volna.customerorder.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface OrderRepository extends JpaRepository<Order,UUID>{
 List<Order> findByCustomerIdOrderByCreatedAtDesc(UUID customerId);
 Optional<Order> findByCustomerIdAndId(UUID customerId,UUID id);
 Optional<Order> findByIdempotencyKey(String key);
}
