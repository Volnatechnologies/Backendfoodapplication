package com.volna.restaurantservice.service.impl;

import com.volna.restaurantservice.dto.CreateOrderRequest;
import com.volna.restaurantservice.dto.OrderItemRequest;
import com.volna.restaurantservice.dto.OrderResponse;
import com.volna.restaurantservice.entity.Order;
import com.volna.restaurantservice.entity.OrderItem;
import com.volna.restaurantservice.entity.OrderStatus;
import com.volna.restaurantservice.entity.Restaurant;
import com.volna.restaurantservice.exception.BadRequestException;
import com.volna.restaurantservice.exception.ResourceNotFoundException;
import com.volna.restaurantservice.repository.OrderItemRepository;
import com.volna.restaurantservice.repository.OrderRepository;
import com.volna.restaurantservice.repository.RestaurantRepository;
import com.volna.restaurantservice.service.OrderService;
import lombok.Builder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@Builder
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final RestaurantRepository restaurantRepository;

    // Explicit constructor
    public OrderServiceImpl(
            OrderRepository orderRepository,
            OrderItemRepository orderItemRepository,
            RestaurantRepository restaurantRepository
    ) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.restaurantRepository = restaurantRepository;
    }

    private static final List<OrderStatus> ACTIVE_STATUSES =
            List.of(
                    OrderStatus.NEW,
                    OrderStatus.PREPARING,
                    OrderStatus.READY
            );

    @Override
    @Transactional
    public OrderResponse createOrder(
            UUID ownerId,
            CreateOrderRequest request
    ) {

        Restaurant restaurant =
                getRestaurantForOwner(ownerId);

        BigDecimal totalAmount =
                request.items()
                        .stream()
                        .map(this::calculateItemTotal)
                        .reduce(
                                BigDecimal.ZERO,
                                BigDecimal::add
                        );

        Order order = Order.builder()
                .id(UUID.randomUUID())
                .restaurantId(restaurant.getId())
                .customerId(request.customerId())
                .customerName(request.customerName().trim())
                .customerPhone(
                        normalize(request.customerPhone())
                )
                .orderType(request.orderType().trim())
                .deliveryAddress(
                        normalize(request.deliveryAddress())
                )
                .totalAmount(totalAmount)
                .status(OrderStatus.NEW)
                .paymentStatus(
                        request.paymentStatus() == null
                                || request.paymentStatus().isBlank()
                                ? "PENDING"
                                : request.paymentStatus().trim()
                )
                .build();

        Order savedOrder =
                orderRepository.save(order);

        List<OrderItem> items =
                request.items()
                        .stream()
                        .map(item ->
                                OrderItem.builder()
                                        .id(UUID.randomUUID())
                                        .orderId(savedOrder.getId())
                                        .name(item.name().trim())
                                        .quantity(item.quantity())
                                        .price(item.price())
                                        .build()
                        )
                        .toList();

        orderItemRepository.saveAll(items);

        return toResponse(savedOrder);
    }

    @Override
    @Transactional(readOnly = true)
    public List<OrderResponse> getActiveOrders(
            UUID ownerId
    ) {

        Restaurant restaurant =
                getRestaurantForOwner(ownerId);

        return orderRepository
                .findByRestaurantIdAndStatusInOrderByCreatedAtDesc(
                        restaurant.getId(),
                        ACTIVE_STATUSES
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public OrderResponse getOrder(
            UUID ownerId,
            UUID orderId
    ) {

        Order order =
                getOrderForRestaurant(
                        ownerId,
                        orderId
                );

        return toResponse(order);
    }

    @Override
    @Transactional
    public OrderResponse updateOrderStatus(
            UUID ownerId,
            UUID orderId,
            OrderStatus newStatus
    ) {

        Order order =
                getOrderForRestaurant(
                        ownerId,
                        orderId
                );

        OrderStatus currentStatus =
                order.getStatus();

        validateStatusTransition(
                currentStatus,
                newStatus
        );

        order.setStatus(newStatus);

        Order savedOrder =
                orderRepository.save(order);

        return toResponse(savedOrder);
    }

    @Override
    @Transactional
    public OrderResponse rejectOrder(
            UUID ownerId,
            UUID orderId,
            String reason
    ) {

        Order order =
                getOrderForRestaurant(
                        ownerId,
                        orderId
                );

        if (order.getStatus() != OrderStatus.NEW) {

            throw new BadRequestException(
                    "Only NEW orders can be declined"
            );
        }

        order.setStatus(OrderStatus.DECLINED);
        order.setRejectionReason(reason.trim());

        Order savedOrder =
                orderRepository.save(order);

        return toResponse(savedOrder);
    }

    private Restaurant getRestaurantForOwner(
            UUID ownerId
    ) {

        return restaurantRepository
                .findByOwnerId(ownerId)
                .orElseThrow(
                        () ->
                                new ResourceNotFoundException(
                                        "Restaurant not found for current user"
                                )
                );
    }

    private Order getOrderForRestaurant(
            UUID ownerId,
            UUID orderId
    ) {

        Restaurant restaurant =
                getRestaurantForOwner(ownerId);

        Order order =
                orderRepository
                        .findById(orderId)
                        .orElseThrow(
                                () ->
                                        new ResourceNotFoundException(
                                                "Order not found"
                                        )
                        );

        if (!order.getRestaurantId()
                .equals(restaurant.getId())) {

            throw new ResourceNotFoundException(
                    "Order not found"
            );
        }

        return order;
    }

    private void validateStatusTransition(
            OrderStatus currentStatus,
            OrderStatus newStatus
    ) {

        if (currentStatus == OrderStatus.NEW
                && newStatus == OrderStatus.PREPARING) {

            return;
        }

        if (currentStatus == OrderStatus.PREPARING
                && newStatus == OrderStatus.READY) {

            return;
        }

        if (currentStatus == OrderStatus.READY
                && newStatus == OrderStatus.DELIVERED) {

            return;
        }

        throw new BadRequestException(
                "Invalid order status transition from "
                        + currentStatus
                        + " to "
                        + newStatus
        );
    }

    private BigDecimal calculateItemTotal(
            OrderItemRequest item
    ) {

        return item.price()
                .multiply(
                        BigDecimal.valueOf(
                                item.quantity()
                        )
                );
    }

    private String normalize(
            String value
    ) {

        if (value == null) {
            return null;
        }

        String trimmed =
                value.trim();

        return trimmed.isEmpty()
                ? null
                : trimmed;
    }

    private OrderResponse toResponse(
            Order order
    ) {

        List<OrderResponse.OrderItemResponse> items =
                orderItemRepository
                        .findByOrderId(order.getId())
                        .stream()
                        .map(
                                item ->
                                        new OrderResponse.OrderItemResponse(
                                                item.getId(),
                                                item.getName(),
                                                item.getQuantity(),
                                                item.getPrice()
                                        )
                        )
                        .toList();

        return new OrderResponse(
                order.getId(),
                order.getRestaurantId(),
                order.getCustomerId(),
                order.getCustomerName(),
                order.getCustomerPhone(),
                order.getOrderType(),
                order.getDeliveryAddress(),
                order.getTotalAmount(),
                order.getStatus(),
                order.getPaymentStatus(),
                order.getRejectionReason(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                items
        );
    }
}