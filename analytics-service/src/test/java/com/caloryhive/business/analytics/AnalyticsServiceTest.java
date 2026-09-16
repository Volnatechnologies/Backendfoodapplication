package com.caloryhive.business.analytics;

import com.caloryhive.business.analytics.dto.AnalyticsSummaryResponse;
import com.caloryhive.business.analytics.dto.EarningsOverviewResponse;
import com.caloryhive.business.analytics.dto.OrderChannelDistributionResponse;
import com.caloryhive.business.analytics.dto.TopSellingItemResponse;
import com.caloryhive.business.analytics.repository.OrderItemRepository;
import com.caloryhive.business.analytics.repository.OrderRepository;
import com.caloryhive.business.analytics.service.impl.AnalyticsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AnalyticsServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private AnalyticsServiceImpl analyticsService;

    private UUID businessId;

    @BeforeEach
    void setUp() {
        businessId = UUID.randomUUID();
    }

    @Test
    void testGetSummaryCalculations() {
        when(orderRepository.countOrdersInPeriod(eq(businessId), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(100L)
                .thenReturn(80L);
        when(orderRepository.sumRevenueInPeriod(eq(businessId), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(new BigDecimal("5000.00"))
                .thenReturn(new BigDecimal("4000.00"));
        when(orderRepository.avgSatisfactionInPeriod(eq(businessId), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(4.85);

        AnalyticsSummaryResponse summary = analyticsService.getSummary(businessId, "30D");

        assertNotNull(summary);
        assertEquals(100L, summary.getTotalOrders());
        assertEquals(new BigDecimal("5000.00"), summary.getTotalRevenue());
        assertEquals(new BigDecimal("50.00"), summary.getAverageOrderValue());
        assertEquals(4.9, summary.getCustomerSatisfaction());
        assertEquals(25.0, summary.getOrderGrowthPercent()); // (100 - 80) / 80 * 100
        assertEquals(25.0, summary.getRevenueGrowthPercent());
    }

    @Test
    void testGetEarnings7D() {
        when(orderRepository.findOrdersForEarnings(eq(businessId), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(Collections.emptyList());

        EarningsOverviewResponse response = analyticsService.getEarnings(businessId, "7D");

        assertNotNull(response);
        assertEquals("7D", response.getPeriod());
        assertFalse(response.getPoints().isEmpty());
        assertTrue(response.getTotalEarnings().compareTo(BigDecimal.ZERO) > 0);
    }

    @Test
    void testGetOrderChannelsDistribution() {
        List<Object[]> channelRows = List.of(
                new Object[]{"DINE_IN", 450L},
                new Object[]{"DELIVERY", 400L},
                new Object[]{"CATERING", 150L}
        );
        when(orderRepository.countOrdersByChannel(eq(businessId), any(OffsetDateTime.class), any(OffsetDateTime.class)))
                .thenReturn(channelRows);

        OrderChannelDistributionResponse result = analyticsService.getOrderChannels(businessId, "30D");

        assertNotNull(result);
        assertEquals(1000L, result.getTotalOrders());
        assertEquals(3, result.getChannels().size());
        assertEquals(45.0, result.getChannels().get(0).getPercentage());
    }

    @Test
    void testGetTopSellingItems() {
        List<Object[]> itemRows = List.of(
                new Object[]{"Truffle Mushroom Burger", "Main Course", 342L, new BigDecimal("5472.00"), "img1.png"},
                new Object[]{"Harvest Quinoa Bowl", "Salads", 289L, new BigDecimal("3468.00"), "img2.png"}
        );
        when(orderItemRepository.findTopSellingItems(businessId)).thenReturn(itemRows);

        List<TopSellingItemResponse> items = analyticsService.getTopSellingItems(businessId, 5);

        assertEquals(2, items.size());
        assertEquals("Truffle Mushroom Burger", items.get(0).getName());
        assertEquals(342L, items.get(0).getOrdersCount());
        assertEquals(100.0, items.get(0).getProgressPercentage());
    }
}
