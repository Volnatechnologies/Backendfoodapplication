package com.volna.restaurantservice.service.impl;

import com.volna.restaurantservice.dto.dashboard.DashboardDocumentStatsResponse;
import com.volna.restaurantservice.dto.dashboard.DashboardMetricsResponse;
import com.volna.restaurantservice.dto.dashboard.DashboardProfileResponse;
import com.volna.restaurantservice.dto.dashboard.DashboardRestaurantResponse;
import com.volna.restaurantservice.dto.dashboard.RestaurantDashboardResponse;
import com.volna.restaurantservice.entity.DocumentStatus;
import com.volna.restaurantservice.entity.Restaurant;
import com.volna.restaurantservice.exception.ResourceNotFoundException;
import com.volna.restaurantservice.repository.RestaurantDocumentRepository;
import com.volna.restaurantservice.repository.RestaurantRepository;
import com.volna.restaurantservice.service.RestaurantDashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RestaurantDashboardServiceImpl implements RestaurantDashboardService {

    private static final BigDecimal ZERO_REVENUE = BigDecimal.ZERO;

    private final RestaurantRepository restaurantRepository;
    private final RestaurantDocumentRepository restaurantDocumentRepository;

    @Override
    @Transactional(readOnly = true)
    public RestaurantDashboardResponse getDashboard(UUID ownerId) {

        Restaurant restaurant = restaurantRepository.findByOwnerId(ownerId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Restaurant not found for current user"
                        )
                );

        DashboardRestaurantResponse restaurantResponse =
                buildRestaurantResponse(restaurant);

        DashboardDocumentStatsResponse documentStats =
                buildDocumentStats(restaurant.getId());

        DashboardProfileResponse profile =
                buildProfileResponse(restaurant);

        DashboardMetricsResponse metrics =
                buildMetrics();

        return new RestaurantDashboardResponse(
                restaurantResponse,
                metrics,
                documentStats,
                profile,
                OffsetDateTime.now(ZoneOffset.UTC)
        );
    }

    private DashboardRestaurantResponse buildRestaurantResponse(
            Restaurant restaurant
    ) {

        return new DashboardRestaurantResponse(
                restaurant.getId(),
                restaurant.getOwnerId(),
                restaurant.getName(),
                restaurant.getDescription(),
                restaurant.getPhone(),
                restaurant.getEmail(),
                restaurant.getAddress(),
                restaurant.getLatitude(),
                restaurant.getLongitude(),
                restaurant.getOpeningTime(),
                restaurant.getClosingTime(),
                restaurant.getStatus()
        );
    }

    private DashboardDocumentStatsResponse buildDocumentStats(
            UUID restaurantId
    ) {

        long total =
                restaurantDocumentRepository.countByRestaurantId(
                        restaurantId
                );

        long approved =
                restaurantDocumentRepository.countByRestaurantIdAndStatus(
                        restaurantId,
                        DocumentStatus.APPROVED
                );

        long pending =
                restaurantDocumentRepository.countByRestaurantIdAndStatus(
                        restaurantId,
                        DocumentStatus.PENDING
                );

        long rejected =
                restaurantDocumentRepository.countByRestaurantIdAndStatus(
                        restaurantId,
                        DocumentStatus.REJECTED
                );

        return new DashboardDocumentStatsResponse(
                total,
                approved,
                pending,
                rejected,
                total > 0 && approved == total
        );
    }

    private DashboardProfileResponse buildProfileResponse(
            Restaurant restaurant
    ) {

        int totalFields = 8;
        int completedFields = 0;
        String nextAction = "Restaurant profile is complete";

        if (hasText(restaurant.getName())) {
            completedFields++;
        } else {
            nextAction = "Add restaurant name";
        }

        if (hasText(restaurant.getPhone())) {
            completedFields++;
        } else if ("Restaurant profile is complete".equals(nextAction)) {
            nextAction = "Add restaurant phone number";
        }

        if (hasText(restaurant.getEmail())) {
            completedFields++;
        } else if ("Restaurant profile is complete".equals(nextAction)) {
            nextAction = "Add restaurant email";
        }

        if (hasText(restaurant.getAddress())) {
            completedFields++;
        } else if ("Restaurant profile is complete".equals(nextAction)) {
            nextAction = "Add restaurant address";
        }

        if (restaurant.getLatitude() != null) {
            completedFields++;
        } else if ("Restaurant profile is complete".equals(nextAction)) {
            nextAction = "Add restaurant latitude";
        }

        if (restaurant.getLongitude() != null) {
            completedFields++;
        } else if ("Restaurant profile is complete".equals(nextAction)) {
            nextAction = "Add restaurant longitude";
        }

        if (restaurant.getOpeningTime() != null) {
            completedFields++;
        } else if ("Restaurant profile is complete".equals(nextAction)) {
            nextAction = "Add opening time";
        }

        if (restaurant.getClosingTime() != null) {
            completedFields++;
        } else if ("Restaurant profile is complete".equals(nextAction)) {
            nextAction = "Add closing time";
        }

        int completionPercentage =
                (int) Math.round(
                        (completedFields * 100.0) / totalFields
                );

        boolean profileComplete =
                completedFields == totalFields;

        return new DashboardProfileResponse(
                completionPercentage,
                profileComplete,
                nextAction
        );
    }

    private DashboardMetricsResponse buildMetrics() {

        /*
         * The current Restaurant Service does not own order/revenue
         * data. These values are intentionally zero until an
         * Order Service is connected.
         */
        return new DashboardMetricsResponse(
                0,
                0,
                0,
                0,
                ZERO_REVENUE,
                ZERO_REVENUE,
                false,
                "ORDER_SERVICE_NOT_CONNECTED"
        );
    }

    private boolean hasText(String value) {
        return value != null && !value.trim().isEmpty();
    }
}