package com.volna.customerorder.repository;
import com.volna.customerorder.entity.OrderStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface OrderStatusHistoryRepository extends JpaRepository<OrderStatusHistory,UUID>{List<OrderStatusHistory> findByOrderIdOrderByCreatedAtAsc(UUID orderId);}
