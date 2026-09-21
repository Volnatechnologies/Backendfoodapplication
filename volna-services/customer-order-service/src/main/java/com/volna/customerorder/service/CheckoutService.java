package com.volna.customerorder.service;
import com.volna.customerorder.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;
@Service @RequiredArgsConstructor
public class CheckoutService {
 private final CartService cart;
 @Value("${app.checkout.tax-rate}") BigDecimal taxRate;
 @Value("${app.checkout.delivery-fee}") BigDecimal deliveryFee;
 @Value("${app.checkout.packaging-fee}") BigDecimal packagingFee;
 public CheckoutPreviewResponse preview(UUID customerId,UUID addressId){
   CartResponse c=cart.get(customerId);
   if(c.items().isEmpty())
       throw new IllegalArgumentException("Cart is empty");
   BigDecimal tax=c.subtotal()
           .multiply(taxRate)
           .setScale(2,RoundingMode.HALF_UP);
   BigDecimal discount=BigDecimal.ZERO.setScale(2);
   BigDecimal total=c.subtotal().add(tax).add(deliveryFee).add(packagingFee).subtract(discount);
   return new CheckoutPreviewResponse(c.items().get(0).restaurantId(),addressId,c.subtotal(),tax,deliveryFee,packagingFee,discount,total);
 }
}
