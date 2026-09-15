package com.volna.inventory_service.service;import com.volna.inventory_service.dto.purchaseorder.*;import com.volna.inventory_service.entity.*;import com.volna.inventory_service.enums.PurchaseOrderStatus;import com.volna.inventory_service.exception.ResourceNotFoundException;import com.volna.inventory_service.repository.*;import org.springframework.stereotype.Service;import org.springframework.transaction.annotation.Transactional;import java.math.BigDecimal;import java.util.UUID;@Service @Transactional public class PurchaseOrderService{private final PurchaseOrderRepository orders;private final InventoryVendorRepository vendors;private final InventoryItemRepository items;private final InventoryRestaurantAccessService access;public PurchaseOrderService(PurchaseOrderRepository o,InventoryVendorRepository v,InventoryItemRepository i,InventoryRestaurantAccessService a){orders=o;vendors=v;items=i;access=a;}public PurchaseOrderResponse create(UUID u,CreatePurchaseOrderRequest q){UUID r=access.restaurant(u);InventoryVendor v=vendors.findByIdAndRestaurantId(q.getVendorId(),r).orElseThrow(()->new ResourceNotFoundException("Vendor not found"));PurchaseOrder o=new PurchaseOrder();o.setRestaurantId(r);o.setVendor(v);o.setStatus(PurchaseOrderStatus.DRAFT);o.setNotes(q.getNotes());BigDecimal total=BigDecimal.ZERO;for(CreatePurchaseOrderItemRequest z:q.getItems()){InventoryItem i=items.findByIdAndRestaurantId(z.getInventoryItemId(),r).orElseThrow(()->new ResourceNotFoundException("Inventory item not found"));PurchaseOrderItem pi=new PurchaseOrderItem();pi.setInventoryItem(i);pi.setQuantity(z.getQuantity());pi.setUnitPrice(z.getUnitPrice());o.addItem(pi);total=total.add(z.getQuantity().multiply(z.getUnitPrice()));}o.setEstimatedTotal(total);PurchaseOrder saved=orders.save(o);
return new PurchaseOrderResponse(
    saved.getId(),
    saved.getVendor().getId(),
    saved.getVendor().getName(),
    saved.getStatus(),
    saved.getEstimatedTotal(),
    saved.getNotes(),
    saved.getCreatedAt(),
    saved.getItems().stream().map(x->new PurchaseOrderItemResponse(
        x.getInventoryItem().getId(),
        x.getInventoryItem().getName(),
        x.getInventoryItem().getSku(),
        x.getQuantity(),
        x.getUnitPrice(),
        x.getQuantity().multiply(x.getUnitPrice())
    )).toList()
);
}}
