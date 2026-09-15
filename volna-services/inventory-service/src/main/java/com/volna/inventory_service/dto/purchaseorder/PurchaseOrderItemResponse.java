package com.volna.inventory_service.dto.purchaseorder;

import java.math.BigDecimal;

public record PurchaseOrderItemResponse(
    Long inventoryItemId,
    String itemName,
    String sku,
    BigDecimal quantity,
    BigDecimal unitPrice,
    BigDecimal lineTotal
) {}
