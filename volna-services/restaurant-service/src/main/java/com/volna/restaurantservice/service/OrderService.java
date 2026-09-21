package com.volna.restaurantservice.service;

import com.volna.restaurantservice.dto.CreateOrderRequest;
import com.volna.restaurantservice.dto.OrderResponse;
import com.volna.restaurantservice.entity.OrderStatus;

import java.util.List;
import java.util.UUID;

public interface OrderService {

    OrderResponse createOrder(
            UUID ownerId,
            CreateOrderRequest request
    );

    List<OrderResponse> getActiveOrders(
            UUID ownerId
    );

    OrderResponse getOrder(
            UUID ownerId,
            UUID orderId
    );

    OrderResponse updateOrderStatus(
            UUID ownerId,
            UUID orderId,
            OrderStatus newStatus
    );

    OrderResponse rejectOrder(
            UUID ownerId,
            UUID orderId,
            String reason
    );
}