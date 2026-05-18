package com.dashboard.service;

import com.dashboard.model.widget.AlertWidgetData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class NotificationService {

    /**
     * Fetches unread alerts/notifications for a user.
     * Cached for 30 seconds — alerts are time-sensitive.
     */
    @Cacheable(value = "widget:alerts", key = "#userId")
    public AlertWidgetData getAlerts(String userId) {
        log.debug("[NotificationService] Fetching alerts for user={}", userId);
        simulateLatency(40, 90);

        List<AlertWidgetData.Alert> alerts = new ArrayList<>();

        alerts.add(AlertWidgetData.Alert.builder()
                .alertId(UUID.randomUUID().toString())
                .message("Deployment pipeline completed successfully")
                .severity("INFO")
                .createdAt(Instant.now().minusSeconds(300))
                .read(false)
                .build());

        alerts.add(AlertWidgetData.Alert.builder()
                .alertId(UUID.randomUUID().toString())
                .message("CPU usage exceeded 85% on prod-server-03")
                .severity("WARN")
                .createdAt(Instant.now().minusSeconds(900))
                .read(false)
                .build());

        alerts.add(AlertWidgetData.Alert.builder()
                .alertId(UUID.randomUUID().toString())
                .message("Database backup failed — immediate attention required")
                .severity("CRITICAL")
                .createdAt(Instant.now().minusSeconds(1800))
                .read(false)
                .build());

        long unread = alerts.stream().filter(a -> !a.isRead()).count();

        return AlertWidgetData.builder()
                .widgetId("alerts")
                .unreadCount((int) unread)
                .alerts(alerts)
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
