package com.caloryhive.business.payment.repository;

import com.caloryhive.business.catering.entity.CateringBooking;
import com.caloryhive.business.payment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PaymentRepository extends JpaRepository<Payment, UUID> {
    List<Payment> findByBookingOrderByCreatedAtDesc(CateringBooking booking);
}
