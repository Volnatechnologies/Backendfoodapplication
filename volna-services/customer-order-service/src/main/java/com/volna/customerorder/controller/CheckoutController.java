package com.volna.customerorder.controller;
import com.volna.customerorder.dto.*;
import com.volna.customerorder.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @RequestMapping("/api/v1/checkout") @RequiredArgsConstructor
public class CheckoutController {
 private final CheckoutService service;
 @PostMapping("/preview") public CheckoutPreviewResponse preview(Authentication a,@Valid @RequestBody CheckoutPreviewRequest r){return service.preview(UUID.fromString(a.getName()),r.addressId());}
 @PostMapping("/buy-now/preview") public CheckoutPreviewResponse buyNowPreview(Authentication a,@Valid @RequestBody CheckoutPreviewRequest r){return service.preview(UUID.fromString(a.getName()),r.addressId());}
}
