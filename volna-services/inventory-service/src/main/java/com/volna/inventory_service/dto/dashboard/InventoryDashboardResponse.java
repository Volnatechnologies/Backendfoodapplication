package com.volna.inventory_service.dto.dashboard;

import java.math.BigDecimal;

public record InventoryDashboardResponse(BigDecimal totalInventoryValue, long totalItems, long lowStockItems,
                                         long outOfStockItems, long expiringSoonItems) {
}
