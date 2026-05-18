package com.dashboard.model.widget;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ChartWidgetData {
    private String          widgetId;
    private String          title;
    private List<DataPoint> series;
    private String          status;
    private Instant         fetchedAt;

    @Data
    @Builder
    public static class DataPoint {
        private String label;
        private double value;
        private String category;
    }

    public static ChartWidgetData fallback() {
        return ChartWidgetData.builder()
                .widgetId("charts")
                .title("Analytics")
                .series(List.of())
                .status("DEGRADED")
                .fetchedAt(Instant.now())
                .build();
    }
}
