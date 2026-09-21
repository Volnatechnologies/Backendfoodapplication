package com.volna.menuservice.controller;
import com.volna.menuservice.dto.*;
import com.volna.menuservice.service.MenuItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.*;
@RestController @RequestMapping("/api/v1/menu/items") @RequiredArgsConstructor
public class MenuItemController {
    private final MenuItemService service;
    @GetMapping("/{id}")
    public MenuItemResponse get(@PathVariable UUID id){
        return service.get(id); }
    @GetMapping
    public List<MenuItemResponse> list(
            @RequestParam UUID restaurantId,
            @RequestParam(defaultValue="true")
            boolean availableOnly){
        return service.list(restaurantId,availableOnly);
    }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public MenuItemResponse create(
            @Valid @RequestBody
            MenuItemCreateRequest r)
    { return service.create(r); }
    @PutMapping("/{id}")
    public MenuItemResponse update(
            @PathVariable UUID id,@Valid
            @RequestBody MenuItemUpdateRequest r){
        return service.update(id,r); }
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id){
        service.delete(id); }
}
