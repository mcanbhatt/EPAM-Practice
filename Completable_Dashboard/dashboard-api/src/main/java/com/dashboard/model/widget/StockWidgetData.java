package com.dashboard.model.widget;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class StockWidgetData {
    private String           widgetId;
    private int              totalItems;
    private int              lowStockCount;
    private int              outOfStockCount;
    private List<StockItem>  items;
    private String           status;
    private Instant          fetchedAt;

    @Data
    @Builder
    public static class StockItem {
        private String sku;
        private String name;
        private int    quantity;
        private int    reorderLevel;
        private String stockStatus;  // OK | LOW | OUT
    }

    public static StockWidgetData fallback() {
        return StockWidgetData.builder()
                .widgetId("stock")
                .totalItems(0)
                .lowStockCount(0)
                .outOfStockCount(0)
                .items(List.of())
                .status("DEGRADED")
                .fetchedAt(Instant.now())
                .build();
    }
}
