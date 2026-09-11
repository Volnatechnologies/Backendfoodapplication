package com.caloryhive.business.staff.controller;

import com.caloryhive.business.staff.dto.CreateShiftRequestDto;
import com.caloryhive.business.staff.dto.ReviewRequestDto;
import com.caloryhive.business.staff.dto.ShiftRequestResponse;
import com.caloryhive.business.staff.entity.enums.RequestStatus;
import com.caloryhive.business.staff.entity.enums.RequestType;
import com.caloryhive.business.staff.service.ShiftRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/api/shift-requests")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ShiftRequestController {

    private final ShiftRequestService shiftRequestService;

    @PostMapping
    public ResponseEntity<ShiftRequestResponse> createRequest(@Valid @RequestBody CreateShiftRequestDto dto) {
        log.info("REST request to create ShiftRequest: type={}, staffId={}", dto.getRequestType(), dto.getStaffId());
        ShiftRequestResponse response = shiftRequestService.createRequest(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ShiftRequestResponse>> getRequests(
            @RequestParam(required = false) RequestStatus status,
            @RequestParam(required = false) UUID staffId,
            @RequestParam(required = false) RequestType type) {
        log.info("REST request to get ShiftRequests - status={}, staffId={}, type={}", status, staffId, type);
        List<ShiftRequestResponse> response = shiftRequestService.getRequests(status, staffId, type);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ShiftRequestResponse> getRequestById(@PathVariable UUID id) {
        log.info("REST request to get ShiftRequest by id={}", id);
        ShiftRequestResponse response = shiftRequestService.getRequestById(id);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/approve")
    public ResponseEntity<ShiftRequestResponse> approveRequest(
            @PathVariable UUID id,
            @RequestBody(required = false) ReviewRequestDto dto) {
        log.info("REST request to approve ShiftRequest id={}", id);
        ShiftRequestResponse response = shiftRequestService.approveRequest(id, dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/deny")
    public ResponseEntity<ShiftRequestResponse> denyRequest(
            @PathVariable UUID id,
            @RequestBody(required = false) ReviewRequestDto dto) {
        log.info("REST request to deny ShiftRequest id={}", id);
        ShiftRequestResponse response = shiftRequestService.denyRequest(id, dto);
        return ResponseEntity.ok(response);
    }
}
