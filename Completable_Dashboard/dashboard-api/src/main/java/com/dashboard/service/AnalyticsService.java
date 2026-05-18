package com.dashboard.service;

import com.dashboard.model.widget.ChartWidgetData;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class AnalyticsService {

    private final MeterRegistry meterRegistry;

    /**
     * Fetches chart/analytics data for the given user.
     * Result is cached in Redis under "widget:charts" for 60 seconds.
     */
    @Cacheable(value = "widget:charts", key = "#userId")
    public ChartWidgetData getCharts(String userId) {
        Timer.Sample sample = Timer.start(meterRegistry);
        try {
            log.debug("[AnalyticsService] Fetching charts for user={}", userId);

            // ── Simulate DB / data-warehouse query ───────────────────
            simulateLatency(100, 200);

            List<ChartWidgetData.DataPoint> series = List.of(
                    ChartWidgetData.DataPoint.builder().label("Jan").value(4200).category("revenue").build(),
                    ChartWidgetData.DataPoint.builder().label("Feb").value(5100).category("revenue").build(),
                    ChartWidgetData.DataPoint.builder().label("Mar").value(4800).category("revenue").build(),
                    ChartWidgetData.DataPoint.builder().label("Apr").value(6300).category("revenue").build(),
                    ChartWidgetData.DataPoint.builder().label("May").value(7100).category("revenue").build(),
                    ChartWidgetData.DataPoint.builder().label("Jun").value(6900).category("revenue").build()
            );

           // throw new RuntimeException("Simulated analytics failure"); // <-- Uncomment to test fallback
            
            return ChartWidgetData.builder()
                    .widgetId("charts")
                    .title("Revenue Trend (6 months)")
                    .series(series)
                    .status("OK")
                    .fetchedAt(Instant.now())
                    .build();

        } catch (Exception ex) {
            log.error("[AnalyticsService] Error fetching charts for userId={}", userId, ex);
            throw new RuntimeException("Analytics service unavailable", ex);
        } finally {
            sample.stop(meterRegistry.timer("widget.charts.latency", "userId", userId));
        }
    }

    private void simulateLatency(int minMs, int maxMs) {
        try {
            long delay = minMs + (long) (Math.random() * (maxMs - minMs));
            Thread.sleep(delay);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
