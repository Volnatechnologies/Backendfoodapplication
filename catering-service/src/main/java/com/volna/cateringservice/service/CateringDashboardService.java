package com.volna.cateringservice.service;

import com.volna.cateringservice.dto.*;
import com.volna.cateringservice.entity.CateringEventStatus;
import com.volna.cateringservice.entity.CateringInquiryStatus;
import com.volna.cateringservice.repository.CateringEventRepository;
import com.volna.cateringservice.repository.CateringInquiryRepository;
import com.volna.cateringservice.repository.CateringPackageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CateringDashboardService {

    private final CateringEventRepository eventRepository;
    private final CateringInquiryRepository inquiryRepository;
    private final CateringPackageRepository packageRepository;
    private final CateringEventService eventService;
    private final CateringInquiryService inquiryService;
    private final CateringPackageService packageService;

    @Transactional(readOnly = true)
    public CateringDashboardResponse getDashboard(String ownerId) {

        LocalDate today = LocalDate.now();

        List<CateringEventResponse> activeBookings =
                eventRepository
                        .findTop5ByOwnerIdAndEventDateGreaterThanEqualOrderByEventDateAscEventTimeAsc(
                                ownerId, today)
                        .stream()
                        .filter(e -> e.getStatus() != CateringEventStatus.CANCELLED)
                        .map(eventService::toResponse)
                        .toList();

        List<CateringInquiryResponse> recentInquiries =
                inquiryRepository.findTop5ByOwnerIdOrderByCreatedAtDesc(ownerId)
                        .stream()
                        .map(inquiryService::toResponse)
                        .toList();

        BigDecimal revenue = calculateRevenue(ownerId);

        return CateringDashboardResponse.builder()
                .upcomingEvents(eventRepository
                        .countByOwnerIdAndEventDateGreaterThanEqual(ownerId, today))
                .totalCateringRevenue(revenue)
                .newInquiries(inquiryRepository
                        .countByOwnerIdAndStatus(
                                ownerId, CateringInquiryStatus.NEW))
                .activeBookings(activeBookings)
                .recentInquiries(recentInquiries)
                .packages(packageRepository
                        .findByOwnerIdAndActiveTrueOrderByNameAsc(ownerId)
                        .stream()
                        .map(packageService::toResponse)
                        .toList())
                .build();
    }

    private BigDecimal calculateRevenue(String ownerId) {
        YearMonth current = YearMonth.now();
        LocalDate start = current.atDay(1);
        LocalDate end = current.atEndOfMonth();

        return eventRepository.findByOwnerIdAndStatusOrderByEventDateAscEventTimeAsc(
                        ownerId, CateringEventStatus.CONFIRMED)
                .stream()
                .filter(e -> !e.getEventDate().isBefore(start)
                        && !e.getEventDate().isAfter(end))
                .map(e -> e.getTotalAmount() == null
                        ? BigDecimal.ZERO : e.getTotalAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
