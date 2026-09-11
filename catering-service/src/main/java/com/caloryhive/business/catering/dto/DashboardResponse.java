package com.caloryhive.business.catering.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponse {
    private long upcomingEvents;
    private BigDecimal totalRevenue;
    private long newInquiries;
    @Builder.Default
    private List<BookingListItemResponse> activeBookings = new ArrayList<>();
    @Builder.Default
    private List<InquirySummaryResponse> recentInquiries = new ArrayList<>();
    @Builder.Default
    private List<MenuPackageResponse> menuPackages = new ArrayList<>();
}
