package com.volna.customerorder.controller;
import com.volna.customerorder.dto.*;
import com.volna.customerorder.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/orders") @RequiredArgsConstructor
public class OrderController {
 private final OrderService service;
 private UUID customer(Authentication a){return UUID.fromString(a.getName());}
 @PostMapping public OrderResponse create(Authentication a,@Valid @RequestBody CreateOrderRequest r){return service.create(customer(a),r);}
 @GetMapping public List<OrderResponse> list(Authentication a){return service.list(customer(a));}
 @GetMapping("/{orderId}") public OrderResponse get(Authentication a,@PathVariable UUID orderId){return service.get(customer(a),orderId);}
 @PostMapping("/{orderId}/cancel") public OrderResponse cancel(Authentication a,@PathVariable UUID orderId){return service.cancel(customer(a),orderId);}
 @GetMapping("/{orderId}/tracking") public TrackingResponse tracking(Authentication a,@PathVariable UUID orderId){return service.tracking(customer(a),orderId);}
 @PostMapping("/{orderId}/payment") public OrderResponse payment(Authentication a,@PathVariable UUID orderId,@Valid @RequestBody PaymentStatusRequest r){return service.updatePayment(customer(a),orderId,r);}
}
