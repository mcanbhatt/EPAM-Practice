package com.dashboard.service;

import com.dashboard.model.widget.StockWidgetData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class InventoryService {

    @Cacheable(value = "widget:stock", key = "#userId")
    public StockWidgetData getStock(String userId) {
        log.debug("[InventoryService] Fetching stock for user={}", userId);
        simulateLatency(80, 150);

        List<StockWidgetData.StockItem> items = List.of(
                StockWidgetData.StockItem.builder()
                        .sku("SKU-001").name("Wireless Keyboard").quantity(245)
                        .reorderLevel(50).stockStatus("OK").build(),
                StockWidgetData.StockItem.builder()
                        .sku("SKU-002").name("USB-C Hub").quantity(18)
                        .reorderLevel(30).stockStatus("LOW").build(),
                StockWidgetData.StockItem.builder()
                        .sku("SKU-003").name("Monitor Stand").quantity(0)
                        .reorderLevel(20).stockStatus("OUT").build(),
                StockWidgetData.StockItem.builder()
                        .sku("SKU-004").name("Webcam HD").quantity(62)
                        .reorderLevel(25).stockStatus("OK").build(),
                StockWidgetData.StockItem.builder()
                        .sku("SKU-005").name("Noise-Cancelling Headset").quantity(8)
                        .reorderLevel(15).stockStatus("LOW").build()
        );

        long lowStock  = items.stream().filter(i -> "LOW".equals(i.getStockStatus())).count();
        long outStock  = items.stream().filter(i -> "OUT".equals(i.getStockStatus())).count();

        return StockWidgetData.builder()
                .widgetId("stock")
                .totalItems(items.size())
                .lowStockCount((int) lowStock)
                .outOfStockCount((int) outStock)
                .items(items)
                .status("OK")
                .fetchedAt(Instant.now())
                .build();
    }

    private void simulateLatency(int minMs, int maxMs) {
        try {
            Thread.sleep(minMs + (long)(Math.random() * (maxMs - minMs)));
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
