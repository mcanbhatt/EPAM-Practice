package com.dashboard.exception;

public class WidgetFetchException extends RuntimeException {

    private final String widgetId;

    public WidgetFetchException(String widgetId, Throwable cause) {
        super("Failed to fetch widget [" + widgetId + "]: " + cause.getMessage(), cause);
        this.widgetId = widgetId;
    }

    public WidgetFetchException(String widgetId, String message) {
        super("Failed to fetch widget [" + widgetId + "]: " + message);
        this.widgetId = widgetId;
    }

    public String getWidgetId() {
        return widgetId;
    }
}
