package com.caloryhive.business.catering.mapper;

import com.caloryhive.business.catering.dto.*;
import com.caloryhive.business.catering.entity.*;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class CateringMapper {

    public BookingDetailsResponse toBookingDetails(CateringBooking booking) {
        if (booking == null) {
            return null;
        }

        FinancialSummaryResponse financials = toFinancialSummary(booking);

        return BookingDetailsResponse.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .eventName(booking.getEventName())
                .eventTypeId(booking.getEventType() != null ? booking.getEventType().getId() : null)
                .eventType(booking.getEventType() != null ? booking.getEventType().getName() : null)
                .date(booking.getEventDate())
                .time(booking.getEventTime())
                .guestCount(booking.getGuestCount())
                .venueAddress(booking.getVenueAddress())
                .specialInstructions(booking.getSpecialInstructions())
                .menuPackage(toMenuPackageSummary(booking.getMenuPackage()))
                .customOptions(booking.getBookingCustomOptions() == null ? Collections.emptyList()
                        : booking.getBookingCustomOptions().stream()
                                .map(option -> toCustomOptionSummary(option.getCustomOption()))
                                .collect(Collectors.toList()))
                .financials(financials)
                .pricing(BookingDetailsResponse.PricingSummary.builder()
                        .basePrice(booking.getBasePrice())
                        .subtotal(booking.getSubtotal())
                        .customOptionsTotal(booking.getCustomOptionsTotal())
                        .serviceFee(booking.getServiceFee())
                        .tax(booking.getTax())
                        .finalTotal(booking.getFinalTotal())
                        .depositRequired(booking.getDepositRequired())
                        .build())
                .status(booking.getStatus())
                .build();
    }

    public BookingListItemResponse toBookingListItem(CateringBooking booking) {
        if (booking == null) {
            return null;
        }
        return BookingListItemResponse.builder()
                .id(booking.getId())
                .bookingCode(booking.getBookingCode())
                .eventName(booking.getEventName())
                .eventType(booking.getEventType() != null ? booking.getEventType().getName() : null)
                .eventDate(booking.getEventDate())
                .eventTime(booking.getEventTime())
                .guestCount(booking.getGuestCount())
                .venueAddress(booking.getVenueAddress())
                .finalTotal(booking.getFinalTotal())
                .status(booking.getStatus())
                .build();
    }

    public FinancialSummaryResponse toFinancialSummary(CateringBooking booking) {
        if (booking == null) {
            return null;
        }
        return FinancialSummaryResponse.builder()
                .guestCount(booking.getGuestCount())
                .basePrice(booking.getBasePrice())
                .customOptionsTotal(booking.getCustomOptionsTotal())
                .subtotal(booking.getSubtotal())
                .serviceFeePercentage(new java.math.BigDecimal("0.10"))
                .serviceFee(booking.getServiceFee())
                .taxPercentage(new java.math.BigDecimal("0.0825"))
                .tax(booking.getTax())
                .finalTotal(booking.getFinalTotal())
                .depositPercentage(booking.getDepositPercentage())
                .depositRequired(booking.getDepositRequired())
                .build();
    }

    public EventTypeResponse toEventTypeResponse(CateringEventType eventType) {
        if (eventType == null) {
            return null;
        }
        return EventTypeResponse.builder()
                .id(eventType.getId())
                .name(eventType.getName())
                .description(eventType.getDescription())
                .active(eventType.isActive())
                .build();
    }

    public MenuPackageResponse toMenuPackageResponse(CateringMenuPackage menuPackage) {
        if (menuPackage == null) {
            return null;
        }
        return MenuPackageResponse.builder()
                .id(menuPackage.getId())
                .name(menuPackage.getName())
                .description(menuPackage.getDescription())
                .pricePerGuest(menuPackage.getPricePerGuest())
                .imageUrl(menuPackage.getImageUrl())
                .active(menuPackage.isActive())
                .build();
    }

    public CustomOptionResponse toCustomOptionResponse(CateringCustomOption customOption) {
        if (customOption == null) {
            return null;
        }
        return CustomOptionResponse.builder()
                .id(customOption.getId())
                .name(customOption.getName())
                .description(customOption.getDescription())
                .price(customOption.getPrice())
                .active(customOption.isActive())
                .build();
    }

    public BookingDetailsResponse.MenuPackageSummary toMenuPackageSummary(CateringMenuPackage menuPackage) {
        if (menuPackage == null) {
            return null;
        }
        return BookingDetailsResponse.MenuPackageSummary.builder()
                .id(menuPackage.getId())
                .name(menuPackage.getName())
                .description(menuPackage.getDescription())
                .pricePerGuest(menuPackage.getPricePerGuest())
                .imageUrl(menuPackage.getImageUrl())
                .build();
    }

    public BookingDetailsResponse.CustomOptionSummary toCustomOptionSummary(CateringCustomOption option) {
        if (option == null) {
            return null;
        }
        return BookingDetailsResponse.CustomOptionSummary.builder()
                .id(option.getId())
                .name(option.getName())
                .description(option.getDescription())
                .price(option.getPrice())
                .build();
    }

    public InquiryResponse toInquiryResponse(CateringInquiry inquiry) {
        if (inquiry == null) {
            return null;
        }
        return InquiryResponse.builder()
                .id(inquiry.getId())
                .name(inquiry.getName())
                .email(inquiry.getEmail())
                .phone(inquiry.getPhone())
                .eventType(inquiry.getEventType())
                .message(inquiry.getMessage())
                .guestCount(inquiry.getGuestCount())
                .eventDate(inquiry.getEventDate())
                .status(inquiry.getStatus())
                .relativeTime(calculateRelativeTime(inquiry.getCreatedAt()))
                .readAt(inquiry.getReadAt())
                .createdAt(inquiry.getCreatedAt())
                .updatedAt(inquiry.getUpdatedAt())
                .build();
    }

    public InquirySummaryResponse toInquirySummaryResponse(CateringInquiry inquiry) {
        if (inquiry == null) {
            return null;
        }
        String snippet = inquiry.getMessage();
        if (snippet != null && snippet.length() > 60) {
            snippet = snippet.substring(0, 57) + "...";
        }
        return InquirySummaryResponse.builder()
                .id(inquiry.getId())
                .title(inquiry.getEventType() != null ? inquiry.getEventType() + " Quote" : "Inquiry")
                .clientName(inquiry.getName())
                .messageSnippet(snippet)
                .eventType(inquiry.getEventType())
                .relativeTime(calculateRelativeTime(inquiry.getCreatedAt()))
                .status(inquiry.getStatus())
                .createdAt(inquiry.getCreatedAt())
                .build();
    }

    public BookingConfirmationResponse toBookingConfirmationResponse(CateringBooking booking) {
        if (booking == null) {
            return null;
        }

        List<BookingConfirmationResponse.NextStepInfo> nextSteps = new ArrayList<>();
        nextSteps.add(BookingConfirmationResponse.NextStepInfo.builder()
                .stepNumber(1)
                .title("Menu Preparation")
                .description("The kitchen team receives the order details and begins prep planning.")
                .icon("utensils")
                .completed(false)
                .build());
        nextSteps.add(BookingConfirmationResponse.NextStepInfo.builder()
                .stepNumber(2)
                .title("Logistics Planning")
                .description("Delivery and staffing schedules are finalized for the event day.")
                .icon("truck")
                .completed(false)
                .build());
        nextSteps.add(BookingConfirmationResponse.NextStepInfo.builder()
                .stepNumber(3)
                .title("Final Check")
                .description("A comprehensive quality assurance check is performed 24 hours prior.")
                .icon("clipboard-check")
                .completed(false)
                .build());
        nextSteps.add(BookingConfirmationResponse.NextStepInfo.builder()
                .stepNumber(4)
                .title("Delivery & Setup")
                .description("The team arrives at " + booking.getVenueAddress() + " at " + booking.getEventTime() + " for setup.")
                .icon("map-pin")
                .completed(false)
                .build());

        List<String> titles = List.of("Menu Preparation", "Logistics Planning", "Final Check", "Delivery & Setup");

        return BookingConfirmationResponse.builder()
                .bookingId(booking.getId())
                .bookingCode(booking.getBookingCode())
                .eventName(booking.getEventName())
                .date(booking.getEventDate())
                .time(booking.getEventTime())
                .totalValue(booking.getFinalTotal())
                .depositPaid(booking.getDepositRequired())
                .venueAddress(booking.getVenueAddress())
                .status(booking.getStatus())
                .nextSteps(nextSteps)
                .stepTitles(titles)
                .build();
    }

    public String calculateRelativeTime(LocalDateTime dateTime) {
        if (dateTime == null) {
            return "";
        }
        Duration duration = Duration.between(dateTime, LocalDateTime.now());
        long seconds = Math.abs(duration.getSeconds());
        if (seconds < 60) {
            return "Just now";
        }
        long minutes = seconds / 60;
        if (minutes < 60) {
            return minutes + "m ago";
        }
        long hours = minutes / 60;
        if (hours < 24) {
            return hours + "h ago";
        }
        long days = hours / 24;
        return days + "d ago";
    }
}
