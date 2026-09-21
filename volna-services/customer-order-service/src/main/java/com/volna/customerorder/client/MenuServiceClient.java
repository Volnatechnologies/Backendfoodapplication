package com.volna.customerorder.client;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import java.math.BigDecimal;
import java.util.UUID;
@Component
public class MenuServiceClient {
 public record MenuItemDto(UUID id,UUID restaurantId,String name,String description,String imageUrl,BigDecimal price,String category,boolean veg,boolean available){}
 private final RestClient client;
 public MenuServiceClient(RestClient.Builder builder,@Value("${services.menu-url}") String baseUrl){
   client=builder.baseUrl(baseUrl).build();
 }
 public MenuItemDto getItem(UUID id){
   return client.get().uri("/api/v1/menu/items/{id}",id).retrieve()
      .onStatus(HttpStatusCode::is4xxClientError,(req,res)->{throw new IllegalArgumentException("Menu item not found: "+id);})
      .body(MenuItemDto.class);
 }
}
