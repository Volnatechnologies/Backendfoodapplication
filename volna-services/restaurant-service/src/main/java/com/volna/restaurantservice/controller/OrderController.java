package com.volna.restaurantservice.controller;

import com.volna.restaurantservice.dto.CreateOrderRequest;
import com.volna.restaurantservice.dto.OrderResponse;
import com.volna.restaurantservice.dto.RejectOrderRequest;
import com.volna.restaurantservice.dto.UpdateOrderStatusRequest;
import com.volna.restaurantservice.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/restaurants/dashboard/orders")
@PreAuthorize("hasRole('RESTAURANT_OWNER')")
public class OrderController {

    private final OrderService orderService;

    // Explicit constructor
    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @PostMapping
    public OrderResponse createOrder(
            Authentication authentication,
            @Valid @RequestBody CreateOrderRequest request
    ) {

        UUID ownerId =
                UUID.fromString(authentication.getName());

        return orderService.createOrder(
                ownerId,
                request
        );
    }

    @GetMapping
    public List<OrderResponse> getActiveOrders(
            Authentication authentication
    ) {

        UUID ownerId =
                UUID.fromString(authentication.getName());

        return orderService.getActiveOrders(
                ownerId
        );
    }

    @GetMapping("/{orderId}")
    public OrderResponse getOrder(
            Authentication authentication,
            @PathVariable UUID orderId
    ) {

        UUID ownerId =
                UUID.fromString(authentication.getName());

        return orderService.getOrder(
                ownerId,
                orderId
        );
    }

    @PatchMapping("/{orderId}/status")
    public OrderResponse updateOrderStatus(
            Authentication authentication,
            @PathVariable UUID orderId,
            @Valid @RequestBody UpdateOrderStatusRequest request
    ) {

        UUID ownerId =
                UUID.fromString(authentication.getName());

        return orderService.updateOrderStatus(
                ownerId,
                orderId,
                request.status()
        );
    }

    @PostMapping("/{orderId}/reject")
    public OrderResponse rejectOrder(
            Authentication authentication,
            @PathVariable UUID orderId,
            @Valid @RequestBody RejectOrderRequest request
    ) {

        UUID ownerId =
                UUID.fromString(authentication.getName());

        return orderService.rejectOrder(
                ownerId,
                orderId,
                request.reason()
        );
    }
}

