package com.volna.restaurantservice.service;
import com.volna.restaurantservice.dto.RestaurantDocumentResponse; import com.volna.restaurantservice.entity.DocumentType; import org.springframework.web.multipart.MultipartFile; import java.util.*;
public interface RestaurantDocumentService{
 RestaurantDocumentResponse upload(UUID ownerId,UUID restaurantId,DocumentType type,MultipartFile file);
 List<RestaurantDocumentResponse> getAll(UUID ownerId,UUID restaurantId);
 void delete(UUID ownerId,UUID restaurantId,UUID documentId);
}