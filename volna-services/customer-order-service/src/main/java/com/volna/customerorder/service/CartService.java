package com.volna.customerorder.service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.volna.customerorder.client.MenuServiceClient;
import com.volna.customerorder.dto.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;
@Service @RequiredArgsConstructor
public class CartService {
 private static final Duration TTL=Duration.ofHours(24);
 private final StringRedisTemplate redis;
 private final ObjectMapper mapper;
 private final MenuServiceClient menu;
 private String key(UUID customerId){return "volna:cart:"+customerId;}
 private record StoredItem(UUID restaurantId,UUID menuItemId,String name,BigDecimal unitPrice,int quantity){}
 private record StoredCart(List<StoredItem> items){}
 public CartResponse get(UUID customerId){
   try{
     String raw=redis.opsForValue().get(key(customerId));
     if(raw==null)return new CartResponse(List.of(),BigDecimal.ZERO.setScale(2));
     StoredCart c=mapper.readValue(raw,StoredCart.class);
     List<CartItemResponse> items=c.items().stream().map(x->new CartItemResponse(x.restaurantId(),x.menuItemId(),x.name(),x.unitPrice(),x.quantity(),x.unitPrice().multiply(BigDecimal.valueOf(x.quantity())))).toList();
     return new CartResponse(items,items.stream().map(CartItemResponse::total).reduce(BigDecimal.ZERO,BigDecimal::add));
   }catch(Exception e){throw new IllegalStateException("Unable to read cart",e);}
 }
 public CartResponse add(UUID customerId,CartItemRequest r){
   MenuServiceClient.MenuItemDto m=menu.getItem(r.menuItemId());
   if(!m.available())throw new IllegalArgumentException("Menu item is currently unavailable");
   if(!m.restaurantId().equals(r.restaurantId()))throw new IllegalArgumentException("Menu item does not belong to the supplied restaurant");
   CartResponse current=get(customerId);
   if(!current.items().isEmpty() && current.items().stream().anyMatch(i->!i.restaurantId().equals(r.restaurantId())))
      throw new IllegalArgumentException("Cart can contain items from one restaurant only");
   List<StoredItem> items=new ArrayList<>();
   for(CartItemResponse i:current.items())items.add(new StoredItem(i.restaurantId(),i.menuItemId(),i.name(),i.unitPrice(),i.quantity()));
   boolean found=false;
   for(int i=0;i<items.size();i++){
     StoredItem x=items.get(i);
     if(x.menuItemId().equals(r.menuItemId())){items.set(i,new StoredItem(x.restaurantId(),x.menuItemId(),m.name(),m.price(),x.quantity()+r.quantity()));found=true;break;}
   }
   if(!found)items.add(new StoredItem(m.restaurantId(),m.id(),m.name(),m.price(),r.quantity()));
   save(customerId,new StoredCart(items)); return get(customerId);
 }
 public CartResponse update(UUID customerId,UUID menuItemId,int quantity){
   if(quantity<1)throw new IllegalArgumentException("Quantity must be at least 1");
   CartResponse current=get(customerId); List<StoredItem> items=new ArrayList<>();
   boolean found=false;
   for(CartItemResponse i:current.items()){
     if(i.menuItemId().equals(menuItemId)){items.add(new StoredItem(i.restaurantId(),i.menuItemId(),i.name(),i.unitPrice(),quantity));found=true;}
     else items.add(new StoredItem(i.restaurantId(),i.menuItemId(),i.name(),i.unitPrice(),i.quantity()));
   }
   if(!found)throw new NoSuchElementException("Item is not in cart");
   save(customerId,new StoredCart(items));return get(customerId);
 }
 public CartResponse remove(UUID customerId,UUID menuItemId){
   CartResponse current=get(customerId);
   List<StoredItem> items=current.items().stream().filter(i->!i.menuItemId().equals(menuItemId))
       .map(i->new StoredItem(i.restaurantId(),i.menuItemId(),i.name(),i.unitPrice(),i.quantity())).toList();
   save(customerId,new StoredCart(items));return get(customerId);
 }
 public void clear(UUID customerId){redis.delete(key(customerId));}
 private void save(UUID id,StoredCart c){
   try{redis.opsForValue().set(key(id),mapper.writeValueAsString(c),TTL);}catch(Exception e){throw new IllegalStateException("Unable to save cart",e);}
 }
}
