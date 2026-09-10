package com.volna.cateringservice.service;

import com.volna.cateringservice.dto.*;
import com.volna.cateringservice.entity.*;
import com.volna.cateringservice.exception.BadRequestException;
import com.volna.cateringservice.exception.ResourceNotFoundException;
import com.volna.cateringservice.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CateringEventService {

    private final CateringEventRepository eventRepository;
    private final CateringEventCustomOptionRepository optionRepository;
    private final CateringPackageService packageService;

    @Value("${app.pricing.service-fee-rate:0.10}")
    private BigDecimal serviceFeeRate;

    @Value("${app.pricing.tax-rate:0.0825}")
    private BigDecimal taxRate;

    @Value("${app.pricing.deposit-rate:0.30}")
    private BigDecimal depositRate;

    @Transactional(readOnly = true)
    public List<CateringEventResponse> getEvents(String ownerId) {
        return eventRepository.findByOwnerIdOrderByEventDateAscEventTimeAsc(ownerId)
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public CateringEvent getOwnedEntity(Long id, String ownerId) {
        return eventRepository.findByIdAndOwnerId(id, ownerId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Catering event not found"));
    }

    @Transactional(readOnly = true)
    public CateringEventResponse get(String ownerId, Long id) {
        return toResponse(getOwnedEntity(id, ownerId));
    }

    public CateringEventResponse create(String ownerId, CateringEventRequest request) {
        CateringPackage cateringPackage =
                packageService.getOwnedEntity(request.getPackageId(), ownerId);

        if (!Boolean.TRUE.equals(cateringPackage.getActive())) {
            throw new BadRequestException("Selected catering package is inactive");
        }

        if (cateringPackage.getMaxGuests() != null
                && request.getGuestCount() > cateringPackage.getMaxGuests()) {
            throw new BadRequestException(
                    "Guest count exceeds the package maximum of "
                            + cateringPackage.getMaxGuests());
        }

        BigDecimal customTotal = request.getCustomOptions().stream()
                .map(CustomOptionRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal subtotal = money(
                cateringPackage.getPricePerGuest()
                        .multiply(BigDecimal.valueOf(request.getGuestCount())));

        BigDecimal serviceFee = money(
                subtotal.add(customTotal).multiply(serviceFeeRate));

        BigDecimal tax = money(
                subtotal.add(customTotal).add(serviceFee).multiply(taxRate));

        BigDecimal total = money(
                subtotal.add(customTotal).add(serviceFee).add(tax));

        BigDecimal deposit = money(total.multiply(depositRate));

        CateringEvent event = CateringEvent.builder()
                .ownerId(ownerId)
                .bookingReference(generateReference())
                .eventName(request.getEventName())
                .eventType(request.getEventType())
                .eventDate(request.getEventDate())
                .eventTime(request.getEventTime())
                .guestCount(request.getGuestCount())
                .venueAddress(request.getVenueAddress())
                .specialInstructions(request.getSpecialInstructions())
                .status(CateringEventStatus.PENDING)
                .cateringPackage(cateringPackage)
                .subtotal(subtotal)
                .customOptionsTotal(money(customTotal))
                .serviceFee(serviceFee)
                .taxAmount(tax)
                .totalAmount(total)
                .depositAmount(deposit)
                .build();

        event = eventRepository.save(event);

        for (CustomOptionRequest option : request.getCustomOptions()) {
            optionRepository.save(CateringEventCustomOption.builder()
                    .event(event)
                    .optionName(option.getName())
                    .amount(money(option.getAmount()))
                    .build());
        }

        return toResponse(event);
    }

    public CateringEventResponse update(
            String ownerId, Long id, CateringEventRequest request) {

        CateringEvent event = getOwnedEntity(id, ownerId);

        if (event.getStatus() == CateringEventStatus.CONFIRMED
                || event.getStatus() == CateringEventStatus.COMPLETED
                || event.getStatus() == CateringEventStatus.CANCELLED) {
            throw new BadRequestException(
                    "Confirmed, completed or cancelled bookings cannot be edited");
        }

        CateringPackage cateringPackage =
                packageService.getOwnedEntity(request.getPackageId(), ownerId);

        if (!Boolean.TRUE.equals(cateringPackage.getActive())) {
            throw new BadRequestException("Selected catering package is inactive");
        }

        BigDecimal customTotal = request.getCustomOptions().stream()
                .map(CustomOptionRequest::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal subtotal = money(
                cateringPackage.getPricePerGuest()
                        .multiply(BigDecimal.valueOf(request.getGuestCount())));

        BigDecimal serviceFee = money(
                subtotal.add(customTotal).multiply(serviceFeeRate));

        BigDecimal tax = money(
                subtotal.add(customTotal).add(serviceFee).multiply(taxRate));

        BigDecimal total = money(
                subtotal.add(customTotal).add(serviceFee).add(tax));

        BigDecimal deposit = money(total.multiply(depositRate));

        event.setEventName(request.getEventName());
        event.setEventType(request.getEventType());
        event.setEventDate(request.getEventDate());
        event.setEventTime(request.getEventTime());
        event.setGuestCount(request.getGuestCount());
        event.setVenueAddress(request.getVenueAddress());
        event.setSpecialInstructions(request.getSpecialInstructions());
        event.setCateringPackage(cateringPackage);
        event.setSubtotal(subtotal);
        event.setCustomOptionsTotal(money(customTotal));
        event.setServiceFee(serviceFee);
        event.setTaxAmount(tax);
        event.setTotalAmount(total);
        event.setDepositAmount(deposit);

        optionRepository.deleteByEventId(event.getId());
        for (CustomOptionRequest option : request.getCustomOptions()) {
            optionRepository.save(CateringEventCustomOption.builder()
                    .event(event)
                    .optionName(option.getName())
                    .amount(money(option.getAmount()))
                    .build());
        }

        return toResponse(eventRepository.save(event));
    }

    public void delete(String ownerId, Long id) {
        CateringEvent event = getOwnedEntity(id, ownerId);

        if (event.getStatus() == CateringEventStatus.CONFIRMED) {
            throw new BadRequestException("Confirmed bookings cannot be deleted");
        }

        eventRepository.delete(event);
    }

    public ConfirmBookingResponse confirm(String ownerId, Long id) {
        CateringEvent event = getOwnedEntity(id, ownerId);

        if (event.getStatus() != CateringEventStatus.PENDING
                && event.getStatus() != CateringEventStatus.DRAFT) {
            throw new BadRequestException(
                    "Only pending or draft bookings can be confirmed");
        }

        event.setStatus(CateringEventStatus.CONFIRMED);
        eventRepository.save(event);

        return ConfirmBookingResponse.builder()
                .id(event.getId())
                .bookingReference(event.getBookingReference())
                .eventName(event.getEventName())
                .eventDate(event.getEventDate())
                .eventTime(event.getEventTime())
                .totalAmount(event.getTotalAmount())
                .depositAmount(event.getDepositAmount())
                .status(event.getStatus())
                .message("Booking confirmed successfully")
                .build();
    }

    public CateringEventResponse toResponse(CateringEvent event) {
        List<CustomOptionResponse> options =
                optionRepository.findByEventIdOrderByIdAsc(event.getId())
                        .stream()
                        .map(o -> CustomOptionResponse.builder()
                                .id(o.getId())
                                .name(o.getOptionName())
                                .amount(o.getAmount())
                                .build())
                        .toList();

        return CateringEventResponse.builder()
                .id(event.getId())
                .bookingReference(event.getBookingReference())
                .eventName(event.getEventName())
                .eventType(event.getEventType())
                .eventDate(event.getEventDate())
                .eventTime(event.getEventTime())
                .guestCount(event.getGuestCount())
                .venueAddress(event.getVenueAddress())
                .specialInstructions(event.getSpecialInstructions())
                .status(event.getStatus())
                .cateringPackage(packageService.toResponse(event.getCateringPackage()))
                .subtotal(event.getSubtotal())
                .customOptionsTotal(event.getCustomOptionsTotal())
                .serviceFee(event.getServiceFee())
                .taxAmount(event.getTaxAmount())
                .totalAmount(event.getTotalAmount())
                .depositAmount(event.getDepositAmount())
                .customOptions(options)
                .build();
    }

    private String generateReference() {
        return "CH-" + UUID.randomUUID()
                .toString().replace("-", "")
                .substring(0, 8).toUpperCase(Locale.ROOT);
    }

    private BigDecimal money(BigDecimal value) {
        return value.setScale(2, RoundingMode.HALF_UP);
    }
}
