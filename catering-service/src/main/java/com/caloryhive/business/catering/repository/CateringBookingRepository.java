package com.caloryhive.business.catering.repository;

import com.caloryhive.business.catering.entity.CateringBooking;
import com.caloryhive.business.common.enums.BookingStatus;
import com.caloryhive.business.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CateringBookingRepository extends JpaRepository<CateringBooking, UUID> {

    Optional<CateringBooking> findByBookingCode(String bookingCode);

    boolean existsByBookingCode(String bookingCode);

    @Query("SELECT b FROM CateringBooking b " +
           "LEFT JOIN FETCH b.eventType " +
           "LEFT JOIN FETCH b.menuPackage " +
           "LEFT JOIN FETCH b.createdBy " +
           "WHERE b.id = :id")
    CateringBooking findByIdWithDetails(@Param("id") UUID id);

    @Query("SELECT b FROM CateringBooking b " +
           "LEFT JOIN FETCH b.eventType " +
           "LEFT JOIN FETCH b.menuPackage " +
           "WHERE b.status = :status " +
           "ORDER BY b.eventDate ASC, b.eventTime ASC")
    List<CateringBooking> findActiveByStatus(@Param("status") BookingStatus status);

    List<CateringBooking> findByStatus(BookingStatus status);

    List<CateringBooking> findByStatusOrderByEventDateAscEventTimeAsc(BookingStatus status);

    Page<CateringBooking> findByStatus(BookingStatus status, Pageable pageable);

    List<CateringBooking> findByEventDate(LocalDate eventDate);

    List<CateringBooking> findByEventDateBetweenOrderByEventDateAscEventTimeAsc(LocalDate startDate, LocalDate endDate);

    List<CateringBooking> findByCreatedBy(User createdBy);

    Page<CateringBooking> findByCreatedByOrderByCreatedAtDesc(User createdBy, Pageable pageable);

    Page<CateringBooking> findByCreatedByIdOrderByCreatedAtDesc(UUID createdById, Pageable pageable);

    @Query("SELECT b FROM CateringBooking b " +
           "LEFT JOIN FETCH b.eventType " +
           "LEFT JOIN FETCH b.menuPackage " +
           "WHERE b.status NOT IN (:excludedStatuses) " +
           "AND b.eventDate >= :fromDate " +
           "ORDER BY b.eventDate ASC, b.eventTime ASC")
    List<CateringBooking> findUpcomingBookings(@Param("excludedStatuses") Collection<BookingStatus> excludedStatuses,
                                              @Param("fromDate") LocalDate fromDate,
                                              Pageable pageable);

    @Query("SELECT b FROM CateringBooking b " +
           "LEFT JOIN FETCH b.eventType " +
           "LEFT JOIN FETCH b.menuPackage " +
           "WHERE b.status NOT IN ('CANCELLED', 'DRAFT') " +
           "ORDER BY b.eventDate ASC, b.eventTime ASC")
    List<CateringBooking> findTopActiveBookings(Pageable pageable);

    @Query("SELECT b FROM CateringBooking b " +
           "LEFT JOIN FETCH b.eventType " +
           "LEFT JOIN FETCH b.menuPackage")
    Page<CateringBooking> findAllWithDetails(Pageable pageable);

    @Query("SELECT b FROM CateringBooking b " +
           "LEFT JOIN FETCH b.eventType " +
           "LEFT JOIN FETCH b.menuPackage " +
           "WHERE (:status IS NULL OR b.status = :status) " +
           "AND (:search IS NULL OR LOWER(b.eventName) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(b.bookingCode) LIKE LOWER(CONCAT('%', :search, '%'))) " +
           "AND (:eventType IS NULL OR LOWER(b.eventType.name) LIKE LOWER(CONCAT('%', :eventType, '%')))")
    Page<CateringBooking> searchBookings(@Param("status") BookingStatus status,
                                        @Param("search") String search,
                                        @Param("eventType") String eventType,
                                        Pageable pageable);

    long countByStatusNotIn(Collection<BookingStatus> statuses);

    long countByStatus(BookingStatus status);

    @Query("SELECT COUNT(b) FROM CateringBooking b " +
           "WHERE b.status NOT IN (:excludedStatuses) " +
           "AND b.eventDate >= :fromDate")
    long countUpcomingEvents(@Param("excludedStatuses") Collection<BookingStatus> excludedStatuses,
                             @Param("fromDate") LocalDate fromDate);

    @Query("SELECT COALESCE(SUM(b.finalTotal), 0) FROM CateringBooking b " +
           "WHERE b.status NOT IN (:excludedStatuses)")
    BigDecimal sumTotalRevenueByStatusNotIn(@Param("excludedStatuses") Collection<BookingStatus> excludedStatuses);
}
