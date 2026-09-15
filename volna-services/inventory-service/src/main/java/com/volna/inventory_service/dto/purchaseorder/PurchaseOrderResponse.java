package com.volna.inventory_service.dto.purchaseorder;

import com.volna.inventory_service.enums.PurchaseOrderStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PurchaseOrderResponse(
    Long id,
    Long vendorId,
    String vendorName,
    PurchaseOrderStatus status,
    BigDecimal estimatedTotal,
    String notes,
    LocalDateTime createdAt,
    List<PurchaseOrderItemResponse> items
) {}
