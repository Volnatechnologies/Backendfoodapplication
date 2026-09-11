package com.caloryhive.business.staff.repository;

import com.caloryhive.business.staff.entity.Shift;
import com.caloryhive.business.staff.entity.enums.ShiftStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface ShiftRepository extends JpaRepository<Shift, UUID> {

    List<Shift> findByShiftDate(LocalDate shiftDate);

    List<Shift> findByShiftDateOrderByStartTimeAsc(LocalDate shiftDate);

    List<Shift> findByShiftDateBetween(LocalDate startDate, LocalDate endDate);

    List<Shift> findByShiftDateBetweenOrderByShiftDateAscStartTimeAsc(LocalDate startDate, LocalDate endDate);

    List<Shift> findByStaffId(UUID staffId);

    List<Shift> findByStaffIdOrderByShiftDateAscStartTimeAsc(UUID staffId);

    List<Shift> findByStaffIdAndShiftDate(UUID staffId, LocalDate shiftDate);

    List<Shift> findByStaffIdAndShiftDateOrderByStartTimeAsc(UUID staffId, LocalDate shiftDate);

    List<Shift> findByStaffIdAndShiftDateBetween(UUID staffId, LocalDate startDate, LocalDate endDate);

    List<Shift> findByStaffIdAndShiftDateBetweenOrderByShiftDateAscStartTimeAsc(UUID staffId, LocalDate startDate, LocalDate endDate);

    List<Shift> findByShiftDateBetweenAndStatus(LocalDate startDate, LocalDate endDate, ShiftStatus status);

    @Query("SELECT s FROM Shift s WHERE s.staff.id = :staffId " +
           "AND s.shiftDate = :shiftDate " +
           "AND (:excludeShiftId IS NULL OR s.id <> :excludeShiftId) " +
           "AND s.status <> com.caloryhive.business.staff.entity.enums.ShiftStatus.CANCELLED " +
           "AND s.startTime < :endTime AND s.endTime > :startTime")
    List<Shift> findOverlappingShifts(@Param("staffId") UUID staffId,
                                      @Param("shiftDate") LocalDate shiftDate,
                                      @Param("startTime") LocalTime startTime,
                                      @Param("endTime") LocalTime endTime,
                                      @Param("excludeShiftId") UUID excludeShiftId);
}
