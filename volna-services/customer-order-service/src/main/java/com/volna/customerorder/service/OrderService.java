package com.volna.customerorder.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volna.customerorder.dto.*;
import com.volna.customerorder.entity.*;
import com.volna.customerorder.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.*;
import java.time.*;
import java.util.*;
@Service @RequiredArgsConstructor
public class OrderService {
 private final CartService cart;
 private final CheckoutService checkout;
 private final OrderRepository orders;
 private final OrderItemRepository items;
 private final OrderStatusHistoryRepository history;
 private final PaymentRepository payments;
 private final KafkaTemplate<String,String> kafka;
 private final ObjectMapper objectMapper;
 @Value("${app.checkout.tax-rate}") BigDecimal taxRate;
 @Value("${app.checkout.delivery-fee}") BigDecimal deliveryFee;
 @Value("${app.checkout.packaging-fee}") BigDecimal packagingFee;

 @Transactional
 public OrderResponse create(UUID customerId,CreateOrderRequest r){
   if(r.idempotencyKey()!=null && !r.idempotencyKey().isBlank()){
      var existing=orders.findByIdempotencyKey(r.idempotencyKey().trim());
      if(existing.isPresent())return response(existing.get());
   }
   CartResponse c=cart.get(customerId);
   if(c.items().isEmpty())throw new IllegalArgumentException("Cart is empty");
   BigDecimal tax=c.subtotal().multiply(taxRate).setScale(2,RoundingMode.HALF_UP);
   BigDecimal discount=BigDecimal.ZERO.setScale(2);
   BigDecimal total=c.subtotal().add(tax).add(deliveryFee).add(packagingFee).subtract(discount);
   UUID restaurantId=c.items().get(0).restaurantId();
   Order o=Order.builder().customerId(customerId).restaurantId(restaurantId).addressId(r.addressId())
       .status(OrderStatus.PAYMENT_PENDING).subtotal(c.subtotal()).tax(tax).deliveryFee(deliveryFee)
       .packagingFee(packagingFee).discount(discount).totalAmount(total).paymentStatus(PaymentStatus.PENDING)
       .idempotencyKey(r.idempotencyKey()==null?null:r.idempotencyKey().trim()).build();
   o=orders.save(o);
   for(CartItemResponse x:c.items()){
      items.save(OrderItem.builder().orderId(o.getId()).menuItemId(x.menuItemId()).itemNameSnapshot(x.name())
          .unitPrice(x.unitPrice()).quantity(x.quantity()).total(x.total()).build());
   }
   history.save(OrderStatusHistory.builder().orderId(o.getId()).newStatus(OrderStatus.PAYMENT_PENDING)
       .reason("Order created").changedBy(customerId.toString()).build());
   payments.save(Payment.builder().orderId(o.getId()).amount(total).status(PaymentStatus.PENDING)
       .idempotencyKey(r.idempotencyKey()).build());
   try{
      String event=objectMapper.writeValueAsString(Map.of("orderId",o.getId(),"customerId",customerId,"restaurantId",restaurantId));
      kafka.send("order.created",o.getId().toString(),event);
   }catch(Exception ignored){}
   cart.clear(customerId);
   return response(o);
 }
 @Transactional(readOnly=true)
 public List<OrderResponse> list(UUID customerId){return orders.findByCustomerIdOrderByCreatedAtDesc(customerId).stream().map(this::response).toList();}
 @Transactional(readOnly=true)
 public OrderResponse get(UUID customerId,UUID id){return response(orders.findByCustomerIdAndId(customerId,id).orElseThrow(()->new NoSuchElementException("Order not found")));}
 @Transactional
 public OrderResponse cancel(UUID customerId,UUID id){
   Order o=orders.findByCustomerIdAndId(customerId,id).orElseThrow(()->new NoSuchElementException("Order not found"));
   if(EnumSet.of(OrderStatus.DELIVERED,OrderStatus.CANCELLED,OrderStatus.OUT_FOR_DELIVERY,OrderStatus.PICKED_UP).contains(o.getStatus()))
      throw new IllegalStateException("Order cannot be cancelled at status "+o.getStatus());
   OrderStatus old=o.getStatus();o.setStatus(OrderStatus.CANCELLED);o.setPaymentStatus(PaymentStatus.REFUNDED);orders.save(o);
   history.save(OrderStatusHistory.builder().orderId(id).oldStatus(old).newStatus(OrderStatus.CANCELLED).reason("Cancelled by customer").changedBy(customerId.toString()).build());
   payments.findByOrderId(id).ifPresent(p->{p.setStatus(PaymentStatus.REFUNDED);payments.save(p);});
   return response(o);
 }
 @Transactional
 public OrderResponse updatePayment(UUID customerId,UUID id,PaymentStatusRequest r){
   Order o=orders.findByCustomerIdAndId(customerId,id).orElseThrow(()->new NoSuchElementException("Order not found"));
   Payment p=payments.findByOrderId(id).orElseThrow(()->new NoSuchElementException("Payment not found"));
   PaymentStatus ps=PaymentStatus.valueOf(r.status().toUpperCase(Locale.ROOT));
   OrderStatus old=o.getStatus();
   if(ps==PaymentStatus.PAID){o.setPaymentStatus(PaymentStatus.PAID);if(old==OrderStatus.PAYMENT_PENDING)o.setStatus(OrderStatus.PAID);}
   else if(ps==PaymentStatus.FAILED){o.setPaymentStatus(PaymentStatus.FAILED);o.setStatus(OrderStatus.PAYMENT_FAILED);}
   else o.setPaymentStatus(ps);
   p.setStatus(ps);p.setProviderReference(r.providerReference());payments.save(p);orders.save(o);
   if(old!=o.getStatus())history.save(OrderStatusHistory.builder().orderId(id).oldStatus(old).newStatus(o.getStatus()).reason("Payment status updated").changedBy(customerId.toString()).build());
   return response(o);
 }
 @Transactional(readOnly=true)
 public TrackingResponse tracking(UUID customerId,UUID id){
   Order o=orders.findByCustomerIdAndId(customerId,id).orElseThrow(()->new NoSuchElementException("Order not found"));
   var h=history.findByOrderIdOrderByCreatedAtAsc(id).stream().map(x->new TrackingResponse.StatusEvent(x.getOldStatus()==null?null:x.getOldStatus().name(),x.getNewStatus().name(),x.getReason(),x.getChangedBy(),x.getCreatedAt())).toList();
   return new TrackingResponse(id,o.getStatus().name(),h);
 }
 private OrderResponse response(Order o){
   var list=items.findByOrderId(o.getId()).stream().map(x->new OrderItemResponse(x.getMenuItemId(),x.getItemNameSnapshot(),x.getUnitPrice(),x.getQuantity(),x.getTotal())).toList();
   return new OrderResponse(o.getId(),o.getCustomerId(),o.getRestaurantId(),o.getAddressId(),o.getStatus().name(),o.getSubtotal(),o.getTax(),o.getDeliveryFee(),o.getPackagingFee(),o.getDiscount(),o.getTotalAmount(),o.getPaymentStatus().name(),list,o.getCreatedAt(),o.getUpdatedAt());
 }
}
