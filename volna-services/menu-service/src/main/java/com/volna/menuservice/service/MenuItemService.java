package com.volna.menuservice.service;
import com.volna.menuservice.dto.*;
import com.volna.menuservice.entity.MenuItem;
import com.volna.menuservice.repository.MenuItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.*;
@Service @RequiredArgsConstructor
public class MenuItemService {
    private final MenuItemRepository repo;
    public MenuItemResponse create(MenuItemCreateRequest r){
        MenuItem x=MenuItem.builder().restaurantId(r.restaurantId()).name(r.name().trim())
            .description(r.description()).imageUrl(r.imageUrl()).price(r.price())
            .category(r.category()).veg(r.veg()).available(r.available()).build();
        return response(repo.save(x));
    }
    public MenuItemResponse get(UUID id){
        return repo.findById(id).map(this::response)
            .orElseThrow(()->new NoSuchElementException("Menu item not found: "+id));
    }
    public List<MenuItemResponse> list(UUID restaurantId, boolean availableOnly){
        return (availableOnly ? repo.findByRestaurantIdAndAvailableTrueOrderByNameAsc(restaurantId)
                : repo.findByRestaurantIdOrderByNameAsc(restaurantId)).stream().map(this::response).toList();
    }
    public MenuItemResponse update(UUID id, MenuItemUpdateRequest r){
        MenuItem x=repo.findById(id).orElseThrow(()->new NoSuchElementException("Menu item not found: "+id));
        x.setName(r.name().trim()); x.setDescription(r.description()); x.setImageUrl(r.imageUrl());
        x.setPrice(r.price()); x.setCategory(r.category()); x.setVeg(r.veg()); x.setAvailable(r.available());
        return response(repo.save(x));
    }
    public void delete(UUID id){ if(!repo.existsById(id)) throw new NoSuchElementException("Menu item not found: "+id); repo.deleteById(id); }
    private MenuItemResponse response(MenuItem x){ return new MenuItemResponse(x.getId(),x.getRestaurantId(),x.getName(),x.getDescription(),x.getImageUrl(),x.getPrice(),x.getCategory(),x.isVeg(),x.isAvailable(),x.getCreatedAt(),x.getUpdatedAt());}
}
