package com.caloryhive.business.staff.service;

import com.caloryhive.business.staff.dto.*;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ShiftService {

    ShiftResponse createShift(CreateShiftRequest request);

    List<ShiftResponse> getShifts(LocalDate date, UUID staffId, LocalDate startDate, LocalDate endDate);

    ShiftResponse getShiftById(UUID id);

    ShiftResponse updateShift(UUID id, UpdateShiftRequest request);

    void deleteShift(UUID id);

    WeeklyScheduleResponse getWeeklySchedule(LocalDate startDate);

    CoverageSummaryResponse getCoverageSummary(LocalDate date);

    CoverageSummaryResponse getCoverageSummary(LocalDate date, LocalDate startDate, LocalDate endDate, Integer requiredStaff);

    PeakHourAnalysisResponse getPeakHourAnalysis(LocalDate date, LocalDate startDate, LocalDate endDate, Integer requiredStaff);

    List<ShiftResponse> publishShifts(LocalDate startDate, LocalDate endDate);
}
