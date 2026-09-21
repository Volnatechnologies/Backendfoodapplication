package com.volna.customerorder.dto;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;
public record TrackingResponse(UUID orderId,String currentStatus,List<StatusEvent> history){
 public record StatusEvent(String oldStatus,String newStatus,String reason,String changedBy,OffsetDateTime createdAt){}
}
