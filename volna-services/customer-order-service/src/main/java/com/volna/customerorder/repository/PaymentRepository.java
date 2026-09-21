package com.volna.customerorder.repository;
import com.volna.customerorder.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface PaymentRepository extends JpaRepository<Payment,UUID>{Optional<Payment> findByOrderId(UUID orderId);}
