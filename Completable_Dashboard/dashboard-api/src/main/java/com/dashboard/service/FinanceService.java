package com.dashboard.service;

import com.dashboard.model.widget.RevenueWidgetData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.List;

@Service
@Slf4j
public class FinanceService {

    /**
     * Fetches revenue widget data. Cached for 2 minutes.
     * This service tends to be slower due to financial aggregation queries.
     */
    @Cacheable(value = "widget:revenue", key = "#userId")
    public RevenueWidgetData getRevenue(String userId) {
        log.debug("[FinanceService] Fetching revenue for user={}", userId);
        simulateLatency(150, 250); // intentionally slower to show parallel benefit

        List<RevenueWidgetData.Period> breakdown = List.of(
                RevenueWidgetData.Period.builder().month("2024-01").amount(new BigDecimal("42000.00")).build(),
                RevenueWidgetData.Period.builder().month("2024-02").amount(new BigDecimal("51000.00")).build(),
                RevenueWidgetData.Period.builder().month("2024-03").amount(new BigDecimal("48000.00")).build(),
                RevenueWidgetData.Period.builder().month("2024-04").amount(new BigDecimal("63000.00")).build(),
                RevenueWidgetData.Period.builder().month("2024-05").amount(new BigDecimal("71000.00")).build(),
                RevenueWidgetData.Period.builder().month("2024-06").amount(new BigDecimal("69000.00")).build()
        );

        BigDecimal total = breakdown.stream()
                .map(RevenueWidgetData.Period::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal lastMonth  = breakdown.get(breakdown.size() - 1).getAmount();
        BigDecimal prevMonth  = breakdown.get(breakdown.size() - 2).getAmount();
        BigDecimal delta      = lastMonth.subtract(prevMonth);
        double     deltaPercent = delta.divide(prevMonth, 4, RoundingMode.HALF_UP)
                                       .multiply(BigDecimal.valueOf(100))
                                       .doubleValue();

        return RevenueWidgetData.builder()
                .widgetId("revenue")
                .totalRevenue(total)
                .delta(delta)
                .deltaPercent(deltaPercent)
                .currency("USD")
                .breakdown(breakdown)
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
