package com.volna.customerorder.controller;
import com.volna.customerorder.dto.*;
import com.volna.customerorder.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;
@RestController @RequestMapping("/api/v1/cart") @RequiredArgsConstructor
public class CartController {
 private final CartService service;
 private UUID customer(Authentication a){return UUID.fromString(a.getName());}
 @GetMapping public CartResponse get(Authentication a){return service.get(customer(a));}
 @PostMapping("/items") public CartResponse add(Authentication a,@Valid @RequestBody CartItemRequest r){return service.add(customer(a),r);}
 @PutMapping("/items/{menuItemId}") public CartResponse update(Authentication a,@PathVariable UUID menuItemId,@RequestParam int quantity){return service.update(customer(a),menuItemId,quantity);}
 @DeleteMapping("/items/{menuItemId}") public CartResponse remove(Authentication a,@PathVariable UUID menuItemId){return service.remove(customer(a),menuItemId);}
 @DeleteMapping public void clear(Authentication a){service.clear(customer(a));}
}
