package com.volna.cateringservice.dto;

import lombok.Builder;
import lombok.Value;

import java.math.BigDecimal;
import java.util.List;

@Value
@Builder
public class CateringDashboardResponse {
    long upcomingEvents;
    BigDecimal totalCateringRevenue;
    long newInquiries;
    List<CateringEventResponse> activeBookings;
    List<CateringInquiryResponse> recentInquiries;
    List<CateringPackageResponse> packages;
}
