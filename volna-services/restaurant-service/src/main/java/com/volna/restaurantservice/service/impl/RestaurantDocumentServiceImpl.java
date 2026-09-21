package com.volna.restaurantservice.service.impl;
import com.volna.restaurantservice.dto.*; import com.volna.restaurantservice.entity.*; import com.volna.restaurantservice.exception.*; import com.volna.restaurantservice.repository.*; import com.volna.restaurantservice.service.RestaurantDocumentService; import com.volna.restaurantservice.storage.StorageService; import lombok.RequiredArgsConstructor; import org.springframework.stereotype.Service; import org.springframework.transaction.annotation.Transactional; import org.springframework.web.multipart.MultipartFile; import java.io.IOException; import java.util.*;
@Service @RequiredArgsConstructor public class RestaurantDocumentServiceImpl implements RestaurantDocumentService{
 private final RestaurantRepository restaurants;
 private final RestaurantDocumentRepository documents;
 private final StorageService storage;
 @Transactional
 public RestaurantDocumentResponse upload(
         UUID ownerId,UUID restaurantId,DocumentType type,MultipartFile file){
  owned(ownerId,restaurantId);
  if(type==null)
   throw new BadRequestException("Document type is required");
  validate(file);
  try{
   RestaurantDocument d=RestaurantDocument.
           builder().
           id(UUID.randomUUID())
           .restaurantId(restaurantId)
           .type(type)
           .fileUrl(storage.store(file))
           .status(DocumentStatus.PENDING)
           .build();
   return response(documents.save(d));
  }
  catch(IOException e){
   throw new BadRequestException("Could not store document");}
 }
 @Transactional(readOnly=true)
 public List<RestaurantDocumentResponse> getAll(UUID ownerId,UUID restaurantId){
  owned(ownerId,restaurantId);
  return documents.findAllByRestaurantId(restaurantId)
          .stream().map(this::response).toList();}
 @Transactional
 public void delete(UUID ownerId,UUID restaurantId,UUID documentId){
  owned(ownerId,restaurantId);
  RestaurantDocument d=documents.findById(documentId)
          .orElseThrow(()->new ResourceNotFoundException("Document not found"));
  if(!d.getRestaurantId().equals(restaurantId))
   throw new ResourceNotFoundException("Document not found");
  documents.delete(d);
 }
 private Restaurant owned(UUID ownerId,UUID id){
  Restaurant r=restaurants.findById(id)
          .orElseThrow(()->new ResourceNotFoundException("Restaurant not found"));
  if(!r.getOwnerId().equals(ownerId))
   throw new ResourceNotFoundException("Restaurant not found");
  return r;
 }
 private void validate(MultipartFile f){
  if(f==null||f.isEmpty())
   throw new BadRequestException("Document file is required");
  if(f.getSize()>10*1024*1024)
   throw new BadRequestException("Maximum document size is 10 MB");
  String t=f.getContentType();
  if(t==null||!(t.equals("application/pdf")||t.equals("image/jpeg")||t.equals("image/png")))
   throw new BadRequestException("Only PDF, JPEG and PNG files are allowed");
 }
 private RestaurantDocumentResponse response(RestaurantDocument d){
  return new RestaurantDocumentResponse(
          d.getId(),d.getRestaurantId(),
          d.getType(),d.getFileUrl(),
          d.getStatus()
          ,d.getUploadedAt());
 }
}