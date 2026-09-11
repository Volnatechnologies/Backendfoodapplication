package com.caloryhive.business.staff.service.impl;

import com.caloryhive.business.common.exception.BadRequestException;
import com.caloryhive.business.common.exception.DuplicateResourceException;
import com.caloryhive.business.common.exception.ResourceNotFoundException;
import com.caloryhive.business.staff.dto.*;
import com.caloryhive.business.staff.entity.Shift;
import com.caloryhive.business.staff.entity.Staff;
import com.caloryhive.business.staff.entity.enums.CoverageStatus;
import com.caloryhive.business.staff.entity.enums.ShiftStatus;
import com.caloryhive.business.staff.mapper.ShiftMapper;
import com.caloryhive.business.staff.repository.ShiftRepository;
import com.caloryhive.business.staff.repository.StaffRepository;
import com.caloryhive.business.staff.service.ShiftService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final StaffRepository staffRepository;
    private final ShiftMapper shiftMapper;

    @Override
    public ShiftResponse createShift(CreateShiftRequest request) {
        log.info("Creating shift for staffId: {}, date: {}", request.getStaffId(), request.getDate());

        Staff staff = validateAndGetStaff(request.getStaffId());
        validateShiftTimingAndDuration(request.getStartTime(), request.getEndTime());
        validateNoOverlap(staff.getId(), request.getDate(), request.getStartTime(), request.getEndTime(), null, staff.getName());

        Shift shift = shiftMapper.toEntity(request, staff);
        Shift savedShift = shiftRepository.save(shift);

        log.info("Successfully created shift with id: {}", savedShift.getId());
        return shiftMapper.toResponse(savedShift);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftResponse> getShifts(LocalDate date, UUID staffId, LocalDate startDate, LocalDate endDate) {
        log.info("Fetching shifts - date: {}, staffId: {}, startDate: {}, endDate: {}", date, staffId, startDate, endDate);

        List<Shift> shifts;

        if (date != null && staffId != null) {
            shifts = shiftRepository.findByStaffIdAndShiftDateOrderByStartTimeAsc(staffId, date);
        } else if (date != null) {
            shifts = shiftRepository.findByShiftDateOrderByStartTimeAsc(date);
        } else if (staffId != null && startDate != null && endDate != null) {
            validateDateRange(startDate, endDate);
            shifts = shiftRepository.findByStaffIdAndShiftDateBetweenOrderByShiftDateAscStartTimeAsc(staffId, startDate, endDate);
        } else if (startDate != null && endDate != null) {
            validateDateRange(startDate, endDate);
            shifts = shiftRepository.findByShiftDateBetweenOrderByShiftDateAscStartTimeAsc(startDate, endDate);
        } else if (staffId != null) {
            shifts = shiftRepository.findByStaffIdOrderByShiftDateAscStartTimeAsc(staffId);
        } else {
            shifts = shiftRepository.findAll();
            shifts.sort(Comparator.comparing(Shift::getShiftDate).thenComparing(Shift::getStartTime));
        }

        return shifts.stream()
                .map(shiftMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftResponse getShiftById(UUID id) {
        log.info("Fetching shift details for id: {}", id);

        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + id));

        return shiftMapper.toResponse(shift);
    }

    @Override
    public ShiftResponse updateShift(UUID id, UpdateShiftRequest request) {
        log.info("Updating shift with id: {}", id);

        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + id));

        Staff staff = validateAndGetStaff(request.getStaffId());
        validateShiftTimingAndDuration(request.getStartTime(), request.getEndTime());
        validateNoOverlap(staff.getId(), request.getDate(), request.getStartTime(), request.getEndTime(), id, staff.getName());

        shiftMapper.updateEntity(shift, request, staff);
        Shift updatedShift = shiftRepository.save(shift);

        log.info("Successfully updated shift with id: {}", updatedShift.getId());
        return shiftMapper.toResponse(updatedShift);
    }

    @Override
    public void deleteShift(UUID id) {
        log.info("Deleting shift with id: {}", id);

        Shift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + id));

        shiftRepository.delete(shift);
        log.info("Successfully deleted shift with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public WeeklyScheduleResponse getWeeklySchedule(LocalDate startDate) {
        LocalDate start = startDate != null ? startDate : LocalDate.now().with(DayOfWeek.MONDAY);
        LocalDate end = start.plusDays(6);

        log.info("Generating weekly schedule from {} to {}", start, end);

        List<Shift> shifts = shiftRepository.findByShiftDateBetweenOrderByShiftDateAscStartTimeAsc(start, end);
        List<Staff> activeStaff = staffRepository.findByActiveTrueOrderByNameAsc();

        Map<UUID, List<Shift>> shiftsByStaffId = shifts.stream()
                .collect(Collectors.groupingBy(s -> s.getStaff().getId()));

        List<StaffWeeklyScheduleResponse> staffSchedules = new ArrayList<>();
        double totalScheduledHours = 0.0;

        for (Staff staff : activeStaff) {
            List<Shift> staffShiftList = shiftsByStaffId.getOrDefault(staff.getId(), Collections.emptyList());
            List<ShiftResponse> shiftResponses = staffShiftList.stream()
                    .map(shiftMapper::toResponse)
                    .toList();

            double staffTotalHours = staffShiftList.stream()
                    .mapToDouble(Shift::getDurationInHours)
                    .sum();

            totalScheduledHours += staffTotalHours;

            staffSchedules.add(StaffWeeklyScheduleResponse.builder()
                    .staffId(staff.getId())
                    .staffName(staff.getName())
                    .staffRole(staff.getRole())
                    .avatarInitials(staff.getAvatarInitials())
                    .totalHours(staffTotalHours)
                    .shifts(shiftResponses)
                    .build());
        }

        return WeeklyScheduleResponse.builder()
                .startDate(start)
                .endDate(end)
                .totalScheduledHours(Math.round(totalScheduledHours * 10.0) / 10.0)
                .budgetHours(160.0)
                .staffSchedules(staffSchedules)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public CoverageSummaryResponse getCoverageSummary(LocalDate date) {
        return getCoverageSummary(date, null, null, 2);
    }

    @Override
    @Transactional(readOnly = true)
    public CoverageSummaryResponse getCoverageSummary(LocalDate date, LocalDate startDate, LocalDate endDate, Integer requiredStaff) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        if (requiredStaff != null && requiredStaff < 0) {
            throw new BadRequestException("Required staff count must not be negative");
        }

        int reqStaff = requiredStaff != null ? requiredStaff : 2;

        LocalDate effStartDate;
        LocalDate effEndDate;
        LocalDate targetDate;

        if (startDate != null && endDate != null) {
            effStartDate = startDate;
            effEndDate = endDate;
            targetDate = startDate;
        } else if (startDate != null) {
            effStartDate = startDate;
            effEndDate = startDate;
            targetDate = startDate;
        } else if (endDate != null) {
            effStartDate = endDate;
            effEndDate = endDate;
            targetDate = endDate;
        } else if (date != null) {
            effStartDate = date;
            effEndDate = date;
            targetDate = date;
        } else {
            effStartDate = LocalDate.now();
            effEndDate = LocalDate.now();
            targetDate = LocalDate.now();
        }

        log.info("Calculating coverage summary from {} to {} with requiredStaff {}", effStartDate, effEndDate, reqStaff);

        double totalScheduledHours;
        double budgetHours;
        double percentage;

        if (startDate == null && endDate == null) {
            LocalDate weekStart = targetDate.with(DayOfWeek.MONDAY);
            LocalDate weekEnd = weekStart.plusDays(6);
            List<Shift> weekShifts = shiftRepository.findByShiftDateBetween(weekStart, weekEnd);
            totalScheduledHours = weekShifts.stream()
                    .filter(s -> s.getStatus() != ShiftStatus.CANCELLED)
                    .mapToDouble(Shift::getDurationInHours)
                    .sum();
            budgetHours = 160.0;
            percentage = budgetHours > 0 ? (totalScheduledHours / budgetHours) * 100.0 : 0.0;
        } else {
            List<Shift> rangeShifts = shiftRepository.findByShiftDateBetween(effStartDate, effEndDate);
            totalScheduledHours = rangeShifts.stream()
                    .filter(s -> s.getStatus() != ShiftStatus.CANCELLED)
                    .mapToDouble(Shift::getDurationInHours)
                    .sum();
            long days = ChronoUnit.DAYS.between(effStartDate, effEndDate) + 1;
            budgetHours = Math.round((days * (160.0 / 7.0)) * 10.0) / 10.0;
            percentage = budgetHours > 0 ? (totalScheduledHours / budgetHours) * 100.0 : 0.0;
        }

        // Calculate legacy peakHourCoverage buckets for targetDate
        List<Shift> targetDateShifts = shiftRepository.findByShiftDate(targetDate).stream()
                .filter(s -> s.getStatus() != ShiftStatus.CANCELLED)
                .toList();

        List<CoverageSummaryResponse.PeakHourBucket> peakBuckets = new ArrayList<>();
        int[] peakHours = {10, 12, 14, 16, 18, 20};
        String[] labels = {"10a", "12p", "2p", "4p", "6p", "8p"};

        for (int i = 0; i < peakHours.length; i++) {
            int h = peakHours[i];
            LocalTime time = LocalTime.of(h, 0);

            long count = targetDateShifts.stream()
                    .filter(s -> !s.getStartTime().isAfter(time) && s.getEndTime().isAfter(time))
                    .count();

            peakBuckets.add(CoverageSummaryResponse.PeakHourBucket.builder()
                    .timeSlot(labels[i])
                    .hour(h)
                    .staffCount(count)
                    .build());
        }

        // Calculate detailed coverage periods for the full range
        List<Shift> activeShifts = shiftRepository.findByShiftDateBetweenOrderByShiftDateAscStartTimeAsc(effStartDate, effEndDate)
                .stream()
                .filter(s -> s.getStatus() != ShiftStatus.CANCELLED)
                .toList();

        List<CoveragePeriodDetail> periods = calculateCoveragePeriods(effStartDate, effEndDate, reqStaff, activeShifts);

        long maxStaff = periods.stream().mapToLong(CoveragePeriodDetail::getScheduledStaff).max().orElse(0L);
        String peakTimeSlot = periods.stream()
                .filter(CoveragePeriodDetail::isPeak)
                .findFirst()
                .map(p -> p.getDate() + " " + p.getTimeSlot())
                .orElse("None");

        int totalPeriods = periods.size();
        int coveredPeriods = (int) periods.stream().filter(p -> p.getCoverageStatus() == CoverageStatus.COVERED).count();
        int partiallyCoveredPeriods = (int) periods.stream().filter(p -> p.getCoverageStatus() == CoverageStatus.PARTIALLY_COVERED).count();
        int uncoveredPeriods = (int) periods.stream().filter(p -> p.getCoverageStatus() == CoverageStatus.UNCOVERED).count();

        return CoverageSummaryResponse.builder()
                .totalScheduledHours(Math.round(totalScheduledHours * 10.0) / 10.0)
                .budgetHours(budgetHours)
                .utilizationPercentage(Math.round(percentage * 10.0) / 10.0)
                .peakHourCoverage(peakBuckets)
                .startDate(effStartDate)
                .endDate(effEndDate)
                .requiredStaff(reqStaff)
                .totalPeriods(totalPeriods)
                .coveredPeriods(coveredPeriods)
                .partiallyCoveredPeriods(partiallyCoveredPeriods)
                .uncoveredPeriods(uncoveredPeriods)
                .coveragePeriods(periods)
                .peakTimeSlot(peakTimeSlot)
                .peakStaffCount(maxStaff)
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public PeakHourAnalysisResponse getPeakHourAnalysis(LocalDate date, LocalDate startDate, LocalDate endDate, Integer requiredStaff) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date cannot be after end date");
        }
        if (requiredStaff != null && requiredStaff < 0) {
            throw new BadRequestException("Required staff count must not be negative");
        }

        int reqStaff = requiredStaff != null ? requiredStaff : 2;

        LocalDate effStartDate;
        LocalDate effEndDate;

        if (startDate != null && endDate != null) {
            effStartDate = startDate;
            effEndDate = endDate;
        } else if (startDate != null) {
            effStartDate = startDate;
            effEndDate = startDate;
        } else if (endDate != null) {
            effStartDate = endDate;
            effEndDate = endDate;
        } else if (date != null) {
            effStartDate = date;
            effEndDate = date;
        } else {
            effStartDate = LocalDate.now();
            effEndDate = LocalDate.now();
        }

        log.info("Calculating peak hour analysis from {} to {} with requiredStaff {}", effStartDate, effEndDate, reqStaff);

        List<Shift> activeShifts = shiftRepository.findByShiftDateBetweenOrderByShiftDateAscStartTimeAsc(effStartDate, effEndDate)
                .stream()
                .filter(s -> s.getStatus() != ShiftStatus.CANCELLED)
                .toList();

        List<CoveragePeriodDetail> periods = calculateCoveragePeriods(effStartDate, effEndDate, reqStaff, activeShifts);

        long maxStaff = periods.stream().mapToLong(CoveragePeriodDetail::getScheduledStaff).max().orElse(0L);
        String peakTimeSlot = periods.stream()
                .filter(CoveragePeriodDetail::isPeak)
                .findFirst()
                .map(p -> p.getDate() + " " + p.getTimeSlot())
                .orElse("None");

        int totalPeriods = periods.size();
        int coveredPeriods = (int) periods.stream().filter(p -> p.getCoverageStatus() == CoverageStatus.COVERED).count();
        int partiallyCoveredPeriods = (int) periods.stream().filter(p -> p.getCoverageStatus() == CoverageStatus.PARTIALLY_COVERED).count();
        int uncoveredPeriods = (int) periods.stream().filter(p -> p.getCoverageStatus() == CoverageStatus.UNCOVERED).count();
        List<CoveragePeriodDetail> peakPeriods = periods.stream().filter(CoveragePeriodDetail::isPeak).toList();

        return PeakHourAnalysisResponse.builder()
                .startDate(effStartDate)
                .endDate(effEndDate)
                .defaultRequiredStaff(reqStaff)
                .peakTimeSlot(peakTimeSlot)
                .peakStaffCount(maxStaff)
                .totalPeriodsAnalyzed(totalPeriods)
                .coveredPeriods(coveredPeriods)
                .partiallyCoveredPeriods(partiallyCoveredPeriods)
                .uncoveredPeriods(uncoveredPeriods)
                .periods(periods)
                .peakPeriods(peakPeriods)
                .build();
    }

    private List<CoveragePeriodDetail> calculateCoveragePeriods(LocalDate startDate, LocalDate endDate, int requiredStaff, List<Shift> activeShifts) {
        List<CoveragePeriodDetail> periods = new ArrayList<>();
        int[] slotStartHours = {10, 12, 14, 16, 18, 20};
        int[] slotEndHours = {12, 14, 16, 18, 20, 22};
        String[] slotLabels = {"10:00 - 12:00", "12:00 - 14:00", "14:00 - 16:00", "16:00 - 18:00", "18:00 - 20:00", "20:00 - 22:00"};

        LocalDate current = startDate;
        while (!current.isAfter(endDate)) {
            final LocalDate day = current;
            List<Shift> dayShifts = activeShifts.stream()
                    .filter(s -> s.getShiftDate().equals(day))
                    .toList();

            for (int i = 0; i < slotStartHours.length; i++) {
                LocalTime slotStart = LocalTime.of(slotStartHours[i], 0);
                LocalTime slotEnd = LocalTime.of(slotEndHours[i], 0);

                long staffCount = dayShifts.stream()
                        .filter(s -> s.getStartTime().isBefore(slotEnd) && s.getEndTime().isAfter(slotStart))
                        .count();

                CoverageStatus status;
                if (requiredStaff == 0) {
                    status = CoverageStatus.COVERED;
                } else if (staffCount >= requiredStaff) {
                    status = CoverageStatus.COVERED;
                } else if (staffCount > 0) {
                    status = CoverageStatus.PARTIALLY_COVERED;
                } else {
                    status = CoverageStatus.UNCOVERED;
                }

                int shortage = Math.max(0, requiredStaff - (int) staffCount);
                int excess = Math.max(0, (int) staffCount - requiredStaff);

                periods.add(CoveragePeriodDetail.builder()
                        .date(day)
                        .timeSlot(slotLabels[i])
                        .startTime(slotStart)
                        .endTime(slotEnd)
                        .requiredStaff(requiredStaff)
                        .scheduledStaff(staffCount)
                        .coverageStatus(status)
                        .shortage(shortage)
                        .excess(excess)
                        .isPeak(false)
                        .build());
            }
            current = current.plusDays(1);
        }

        long maxStaff = periods.stream().mapToLong(CoveragePeriodDetail::getScheduledStaff).max().orElse(0L);
        if (maxStaff > 0) {
            for (CoveragePeriodDetail p : periods) {
                if (p.getScheduledStaff() == maxStaff) {
                    p.setPeak(true);
                }
            }
        }

        return periods;
    }

    @Override
    public List<ShiftResponse> publishShifts(LocalDate startDate, LocalDate endDate) {
        log.info("Publishing shifts between {} and {}", startDate, endDate);

        if (startDate == null || endDate == null) {
            LocalDate today = LocalDate.now();
            startDate = today.with(DayOfWeek.MONDAY);
            endDate = startDate.plusDays(6);
        }

        List<Shift> drafts = shiftRepository.findByShiftDateBetweenAndStatus(startDate, endDate, ShiftStatus.DRAFT);
        for (Shift s : drafts) {
            s.setStatus(ShiftStatus.PUBLISHED);
        }

        List<Shift> saved = shiftRepository.saveAll(drafts);
        log.info("Successfully published {} shifts", saved.size());

        return saved.stream()
                .map(shiftMapper::toResponse)
                .toList();
    }

    // =========================================================================
    // HELPER VALIDATION METHODS
    // =========================================================================

    private Staff validateAndGetStaff(UUID staffId) {
        if (staffId == null) {
            throw new BadRequestException("Staff ID must not be null");
        }
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with id: " + staffId));

        if (Boolean.FALSE.equals(staff.getActive())) {
            throw new BadRequestException("Cannot assign shifts to inactive or deleted staff member: " + staff.getName());
        }

        return staff;
    }

    private void validateShiftTimingAndDuration(LocalTime startTime, LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new BadRequestException("Start time and end time are required");
        }

        if (!startTime.isBefore(endTime)) {
            throw new BadRequestException("Start time (" + startTime + ") must be before end time (" + endTime + "). Overnight shifts across midnight are not supported.");
        }

        long durationMinutes = Duration.between(startTime, endTime).toMinutes();
        if (durationMinutes < 30) {
            throw new BadRequestException("Shift duration must be at least 30 minutes (provided: " + durationMinutes + " mins)");
        }

        if (durationMinutes > 16 * 60) {
            throw new BadRequestException("Shift duration cannot exceed 16 hours in a single shift (provided: " + (durationMinutes / 60.0) + " hours)");
        }
    }

    private void validateNoOverlap(UUID staffId, LocalDate date, LocalTime startTime, LocalTime endTime, UUID excludeShiftId, String staffName) {
        if (date == null) {
            throw new BadRequestException("Shift date is required");
        }

        List<Shift> overlapping = shiftRepository.findOverlappingShifts(staffId, date, startTime, endTime, excludeShiftId);
        if (!overlapping.isEmpty()) {
            Shift conflict = overlapping.get(0);
            throw new DuplicateResourceException(String.format(
                    "Shift overlaps with existing shift for %s on %s (%s - %s)",
                    staffName, date, conflict.getStartTime(), conflict.getEndTime()
            ));
        }
    }

    private void validateDateRange(LocalDate startDate, LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new BadRequestException("Start date (" + startDate + ") cannot be after end date (" + endDate + ")");
        }
    }
}
