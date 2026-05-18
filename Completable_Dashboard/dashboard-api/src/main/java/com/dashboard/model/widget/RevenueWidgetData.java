package com.dashboard.model.widget;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Data
@Builder
public class RevenueWidgetData {
    private String          widgetId;
    private BigDecimal      totalRevenue;
    private BigDecimal      delta;          // vs last period
    private double          deltaPercent;
    private String          currency;
    private List<Period>    breakdown;
    private String          status;
    private Instant         fetchedAt;

    @Data
    @Builder
    public static class Period {
        private String     month;           // e.g. "2024-03"
        private BigDecimal amount;
    }

    public static RevenueWidgetData fallback() {
        return RevenueWidgetData.builder()
                .widgetId("revenue")
                .totalRevenue(BigDecimal.ZERO)
                .delta(BigDecimal.ZERO)
                .currency("USD")
                .breakdown(List.of())
                .status("DEGRADED")
                .fetchedAt(Instant.now())
                .build();
    }
}
