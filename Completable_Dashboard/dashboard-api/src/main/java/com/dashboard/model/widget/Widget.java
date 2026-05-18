package com.dashboard.model.widget;

import java.time.Instant;

/**
 * Generic contract every widget must implement.
 *
 * @param <T> the widget payload type
 */
public interface Widget<T> {

    /** Unique identifier for this widget type, e.g. "charts" */
    String getWidgetId();

    /** The data payload */
    T getData();

    /** Metadata about fetch time, status, TTL */
    WidgetMeta getMeta();

    /** True if the cached value is past its soft-TTL */
    boolean isStale();

    /** Degraded fallback returned when the service times out or errors */
    T fallback();

    // ------------------------------------------------------------------
    record WidgetMeta(
            String  widgetId,
            String  title,
            Instant fetchedAt,
            long    ttlSeconds,
            String  status       // OK | TIMEOUT | DEGRADED
    ) {}
}
