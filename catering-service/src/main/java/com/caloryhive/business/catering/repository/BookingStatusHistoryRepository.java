package com.caloryhive.business.catering.repository;

import com.caloryhive.business.catering.entity.BookingStatusHistory;
import com.caloryhive.business.catering.entity.CateringBooking;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BookingStatusHistoryRepository extends JpaRepository<BookingStatusHistory, UUID> {
    List<BookingStatusHistory> findByBookingOrderByChangedAtAsc(CateringBooking booking);

    List<BookingStatusHistory> findByBookingIdOrderByChangedAtAsc(UUID bookingId);
}
