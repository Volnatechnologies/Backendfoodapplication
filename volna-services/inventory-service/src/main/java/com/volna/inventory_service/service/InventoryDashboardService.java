package com.volna.inventory_service.service;

import com.volna.inventory_service.dto.dashboard.*;
import com.volna.inventory_service.entity.InventoryItem;
import com.volna.inventory_service.enums.InventoryStatus;
import com.volna.inventory_service.repository.InventoryItemRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class InventoryDashboardService {
    private final InventoryItemRepository repo;
    private final InventoryRestaurantAccessService access;

    public InventoryDashboardService(InventoryItemRepository r, InventoryRestaurantAccessService a) {
        repo = r;
        access = a;
    }

    private List<InventoryItem> items(UUID u) {
        return repo.findByRestaurantId(access.restaurant(u), org.springframework.data.domain.Pageable.unpaged()).getContent();
    }

    public InventoryDashboardResponse dashboard(UUID u) {
        var xs = items(u);
        BigDecimal value = xs.stream().map(x -> x.getStockLevel().multiply(x.getUnitCost())).reduce(BigDecimal.ZERO, BigDecimal::add);
        LocalDate d = LocalDate.now(), e = d.plusDays(7);
        long exp = xs.stream().filter(x -> x.getExpiryDate() != null && !x.getExpiryDate().isBefore(d) && !x.getExpiryDate().isAfter(e)).count();
        return new InventoryDashboardResponse(value, xs.size(), xs.stream().filter(x -> x.getStatus() == InventoryStatus.LOW_STOCK).count(), xs.stream().filter(x -> x.getStatus() == InventoryStatus.OUT_OF_STOCK).count(), exp);
    }

    public InventoryInsightResponse insights(UUID u) {
        var xs = items(u);
        List<InventoryInsightResponse.RecommendedItem> rec = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;
        for (var x : xs) {
            if (x.getStatus() == InventoryStatus.OUT_OF_STOCK || x.getStatus() == InventoryStatus.LOW_STOCK) {
                BigDecimal q = x.getStatus() == InventoryStatus.OUT_OF_STOCK ? x.getMinimumStock() : x.getMinimumStock().subtract(x.getStockLevel());
                if (q.signum() > 0) {
                    rec.add(new InventoryInsightResponse.RecommendedItem(x.getName(), q, x.getUnit()));
                    total = total.add(q.multiply(x.getUnitCost()));
                }
            }
        }
        return new InventoryInsightResponse("Recommended Replenishment", rec.isEmpty() ? "LOW" : "HIGH", "Based on current stock levels, consider replenishing the items below.", rec, total);
    }

    public List<InventoryItem> expiring(UUID u, int days) {
        LocalDate d = LocalDate.now();
        return repo.findByRestaurantIdAndExpiryDateBetween(access.restaurant(u), d, d.plusDays(days));
    }
}
