package com.dashboard.model.widget;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class AlertWidgetData {
    private String       widgetId;
    private int          unreadCount;
    private List<Alert>  alerts;
    private String       status;
    private Instant      fetchedAt;

    @Data
    @Builder
    public static class Alert {
        private String  alertId;
        private String  message;
        private String  severity;   // INFO | WARN | CRITICAL
        private Instant createdAt;
        private boolean read;
    }

    public static AlertWidgetData fallback() {
        return AlertWidgetData.builder()
                .widgetId("alerts")
                .unreadCount(0)
                .alerts(List.of())
                .status("DEGRADED")
                .fetchedAt(Instant.now())
                .build();
    }
}
