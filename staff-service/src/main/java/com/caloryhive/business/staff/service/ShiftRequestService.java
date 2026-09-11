package com.caloryhive.business.staff.service;

import com.caloryhive.business.staff.dto.CreateShiftRequestDto;
import com.caloryhive.business.staff.dto.ReviewRequestDto;
import com.caloryhive.business.staff.dto.ShiftRequestResponse;
import com.caloryhive.business.staff.entity.enums.RequestStatus;
import com.caloryhive.business.staff.entity.enums.RequestType;

import java.util.List;
import java.util.UUID;

public interface ShiftRequestService {

    ShiftRequestResponse createRequest(CreateShiftRequestDto dto);

    List<ShiftRequestResponse> getRequests(RequestStatus status, UUID staffId, RequestType type);

    ShiftRequestResponse getRequestById(UUID id);

    ShiftRequestResponse approveRequest(UUID id, ReviewRequestDto dto);

    ShiftRequestResponse denyRequest(UUID id, ReviewRequestDto dto);
}
