package com.caloryhive.business.catering.repository;

import com.caloryhive.business.catering.entity.BookingCustomOption;
import com.caloryhive.business.catering.entity.CateringBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface BookingCustomOptionRepository extends JpaRepository<BookingCustomOption, UUID> {
    List<BookingCustomOption> findByBooking(CateringBooking booking);

    List<BookingCustomOption> findByBookingId(UUID bookingId);

    @Query("SELECT bco FROM BookingCustomOption bco JOIN FETCH bco.customOption WHERE bco.booking.id = :bookingId")
    List<BookingCustomOption> findByBookingIdWithOptionDetails(@Param("bookingId") UUID bookingId);

    void deleteByBooking(CateringBooking booking);
}
