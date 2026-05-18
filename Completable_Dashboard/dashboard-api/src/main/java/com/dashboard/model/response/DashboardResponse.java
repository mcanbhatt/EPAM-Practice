package com.dashboard.model.response;

import com.dashboard.model.widget.*;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.Map;

@Data
@Builder
public class DashboardResponse {

    private ChartWidgetData   charts;
    private ProfileWidgetData profile;
    private AlertWidgetData   alerts;
    private RevenueWidgetData revenue;
    private StockWidgetData   stock;
    private TaskWidgetData    tasks;

    private Instant              generatedAt;
    private long                 loadTimeMs;
    private Map<String, String>  widgetStatuses;  // per-widget: OK | TIMEOUT | DEGRADED
}
