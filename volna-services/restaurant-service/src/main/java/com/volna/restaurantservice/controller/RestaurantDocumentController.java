package com.volna.restaurantservice.controller;
import com.volna.restaurantservice.dto.RestaurantDocumentResponse;
import com.volna.restaurantservice.entity.DocumentType;
import com.volna.restaurantservice.service.RestaurantDocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.*;
@RestController @RequestMapping("/api/v1/restaurants/{restaurantId}/documents")
@RequiredArgsConstructor
public class RestaurantDocumentController{
 private final RestaurantDocumentService service;
 @PostMapping(consumes="multipart/form-data")
 @ResponseStatus(HttpStatus.CREATED)
 @PreAuthorize("hasRole('RESTAURANT_OWNER')")
 public RestaurantDocumentResponse upload(@PathVariable UUID restaurantId,
                                          @RequestParam DocumentType type,
                                          @RequestPart("file") MultipartFile file,Authentication a){
  return service.upload(id(a),restaurantId,type,file);
 }
 @GetMapping
 @PreAuthorize("hasRole('RESTAURANT_OWNER')")
 public List<RestaurantDocumentResponse> all(@PathVariable UUID restaurantId,Authentication a){
  return service.getAll(id(a),restaurantId);}
 @DeleteMapping("/{documentId}")
 @ResponseStatus(HttpStatus.NO_CONTENT)
 @PreAuthorize("hasRole('RESTAURANT_OWNER')")
 public void delete(@PathVariable UUID restaurantId,@PathVariable UUID documentId,Authentication a){
  service.delete(id(a),restaurantId,documentId);
 }
 private UUID id(Authentication a){
  return UUID.fromString(a.getName());
 }
}