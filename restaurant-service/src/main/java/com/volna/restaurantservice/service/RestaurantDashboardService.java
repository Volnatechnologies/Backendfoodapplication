package com.volna.restaurantservice.service;

import com.volna.restaurantservice.dto.dashboard.RestaurantDashboardResponse;

import java.util.UUID;

public interface RestaurantDashboardService {

    RestaurantDashboardResponse getDashboard(UUID ownerId);
}