package com.caloryhive.business.staff.service.impl;

import com.caloryhive.business.common.exception.BadRequestException;
import com.caloryhive.business.common.exception.ConflictException;
import com.caloryhive.business.common.exception.ResourceNotFoundException;
import com.caloryhive.business.staff.dto.CreateShiftRequestDto;
import com.caloryhive.business.staff.dto.ReviewRequestDto;
import com.caloryhive.business.staff.dto.ShiftRequestResponse;
import com.caloryhive.business.staff.entity.Shift;
import com.caloryhive.business.staff.entity.ShiftRequest;
import com.caloryhive.business.staff.entity.Staff;
import com.caloryhive.business.staff.entity.enums.RequestStatus;
import com.caloryhive.business.staff.entity.enums.RequestType;
import com.caloryhive.business.staff.entity.enums.ShiftStatus;
import com.caloryhive.business.staff.mapper.ShiftRequestMapper;
import com.caloryhive.business.staff.repository.ShiftRepository;
import com.caloryhive.business.staff.repository.ShiftRequestRepository;
import com.caloryhive.business.staff.repository.StaffRepository;
import com.caloryhive.business.staff.service.ShiftRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ShiftRequestServiceImpl implements ShiftRequestService {

    private final ShiftRequestRepository shiftRequestRepository;
    private final StaffRepository staffRepository;
    private final ShiftRepository shiftRepository;
    private final ShiftRequestMapper shiftRequestMapper;

    @Override
    public ShiftRequestResponse createRequest(CreateShiftRequestDto dto) {
        log.info("Creating shift request for staffId: {}, type: {}", dto.getStaffId(), dto.getRequestType());

        Staff staff = validateAndGetStaff(dto.getStaffId());

        Shift shift = null;
        Staff targetStaff = null;

        if (dto.getRequestType() == RequestType.SHIFT_SWAP) {
            if (dto.getShiftId() == null) {
                throw new BadRequestException("Shift ID is required for shift swap requests");
            }
            if (dto.getTargetStaffId() == null) {
                throw new BadRequestException("Target staff ID is required for shift swap requests");
            }
            if (staff.getId().equals(dto.getTargetStaffId())) {
                throw new BadRequestException("Cannot swap shift with yourself");
            }

            shift = shiftRepository.findById(dto.getShiftId())
                    .orElseThrow(() -> new ResourceNotFoundException("Shift not found with id: " + dto.getShiftId()));

            if (!shift.getStaff().getId().equals(staff.getId())) {
                throw new BadRequestException("Shift does not belong to the requesting staff member");
            }

            targetStaff = validateAndGetStaff(dto.getTargetStaffId());

            // Validate target staff doesn't already have an overlapping shift
            List<Shift> targetOverlaps = shiftRepository.findOverlappingShifts(
                    targetStaff.getId(),
                    shift.getShiftDate(),
                    shift.getStartTime(),
                    shift.getEndTime(),
                    null
            );

            if (!targetOverlaps.isEmpty()) {
                throw new ConflictException("Cannot swap: Target staff member " + targetStaff.getName() + " already has an overlapping shift on " + shift.getShiftDate());
            }

        } else if (dto.getRequestType() == RequestType.TIME_OFF) {
            if (dto.getRequestedDate() == null) {
                throw new BadRequestException("Requested date is required for time-off requests");
            }
            if (dto.getEndDate() != null && dto.getEndDate().isBefore(dto.getRequestedDate())) {
                throw new BadRequestException("Time-off end date cannot be before start date");
            }
            if (dto.getReason() == null || dto.getReason().trim().isEmpty()) {
                throw new BadRequestException("Reason is required for time-off requests");
            }
        }

        ShiftRequest request = ShiftRequest.builder()
                .staff(staff)
                .requestType(dto.getRequestType())
                .shift(shift)
                .targetStaff(targetStaff)
                .requestedDate(dto.getRequestedDate() != null ? dto.getRequestedDate() : (shift != null ? shift.getShiftDate() : null))
                .endDate(dto.getEndDate())
                .reason(dto.getReason() != null ? dto.getReason().trim() : null)
                .status(RequestStatus.PENDING)
                .build();

        ShiftRequest saved = shiftRequestRepository.save(request);
        log.info("Successfully created shift request with id: {}", saved.getId());
        return shiftRequestMapper.toResponse(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShiftRequestResponse> getRequests(RequestStatus status, UUID staffId, RequestType type) {
        log.info("Fetching shift requests - status: {}, staffId: {}, type: {}", status, staffId, type);

        List<ShiftRequest> requests = shiftRequestRepository.findWithFilters(status, staffId, type);
        return requests.stream()
                .map(shiftRequestMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public ShiftRequestResponse getRequestById(UUID id) {
        log.info("Fetching shift request with id: {}", id);

        ShiftRequest request = shiftRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift request not found with id: " + id));

        return shiftRequestMapper.toResponse(request);
    }

    @Override
    public ShiftRequestResponse approveRequest(UUID id, ReviewRequestDto dto) {
        log.info("Approving shift request with id: {}", id);

        ShiftRequest request = shiftRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift request not found with id: " + id));

        validatePendingStatus(request, "approve");

        if (request.getRequestType() == RequestType.SHIFT_SWAP) {
            Shift shift = request.getShift();
            Staff targetStaff = request.getTargetStaff();

            if (shift == null || targetStaff == null) {
                throw new BadRequestException("Cannot approve shift swap: associated shift or target staff is missing");
            }

            // Validate that the swap will not create an overlapping shift for the target staff
            List<Shift> targetOverlaps = shiftRepository.findOverlappingShifts(
                    targetStaff.getId(),
                    shift.getShiftDate(),
                    shift.getStartTime(),
                    shift.getEndTime(),
                    shift.getId()
            );
            if (!targetOverlaps.isEmpty()) {
                throw new ConflictException("Cannot approve shift swap: Target staff member " + targetStaff.getName() + " has an overlapping shift on " + shift.getShiftDate());
            }

            // Reassign shift to the target staff member
            log.info("Reassigning shift {} from {} to {}", shift.getId(), request.getStaff().getName(), targetStaff.getName());
            shift.setStaff(targetStaff);
            shiftRepository.save(shift);

        } else if (request.getRequestType() == RequestType.TIME_OFF) {
            // Cancel any active shifts scheduled for the requester during the approved time-off period
            LocalDate start = request.getRequestedDate();
            LocalDate end = request.getEndDate() != null ? request.getEndDate() : start;

            List<Shift> scheduledShifts = shiftRepository.findByStaffIdAndShiftDateBetween(request.getStaff().getId(), start, end);
            for (Shift s : scheduledShifts) {
                log.info("Cancelling shift {} for {} due to approved time off", s.getId(), request.getStaff().getName());
                s.setStatus(ShiftStatus.CANCELLED);
                shiftRepository.save(s);
            }
        }

        request.setStatus(RequestStatus.APPROVED);
        if (dto != null && dto.getAdminComment() != null) {
            request.setAdminComment(dto.getAdminComment().trim());
        }

        ShiftRequest saved = shiftRequestRepository.save(request);
        log.info("Successfully approved shift request with id: {}", saved.getId());
        return shiftRequestMapper.toResponse(saved);
    }

    @Override
    public ShiftRequestResponse denyRequest(UUID id, ReviewRequestDto dto) {
        log.info("Denying shift request with id: {}", id);

        ShiftRequest request = shiftRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Shift request not found with id: " + id));

        validatePendingStatus(request, "deny");

        request.setStatus(RequestStatus.DENIED);
        if (dto != null && dto.getAdminComment() != null) {
            request.setAdminComment(dto.getAdminComment().trim());
        }

        ShiftRequest saved = shiftRequestRepository.save(request);
        log.info("Successfully denied shift request with id: {}", saved.getId());
        return shiftRequestMapper.toResponse(saved);
    }

    private Staff validateAndGetStaff(UUID staffId) {
        if (staffId == null) {
            throw new BadRequestException("Staff ID is required");
        }
        Staff staff = staffRepository.findById(staffId)
                .orElseThrow(() -> new ResourceNotFoundException("Staff member not found with id: " + staffId));

        if (Boolean.FALSE.equals(staff.getActive())) {
            throw new BadRequestException("Cannot process request for inactive staff member: " + staff.getName());
        }

        return staff;
    }

    private void validatePendingStatus(ShiftRequest request, String action) {
        if (request.getStatus() == RequestStatus.APPROVED) {
            throw new ConflictException("Cannot " + action + " an already approved request");
        }
        if (request.getStatus() == RequestStatus.DENIED) {
            throw new ConflictException("Cannot " + action + " an already denied request");
        }
        if (request.getStatus() != RequestStatus.PENDING) {
            throw new ConflictException("Only PENDING requests can be " + action + "ed");
        }
    }
}
