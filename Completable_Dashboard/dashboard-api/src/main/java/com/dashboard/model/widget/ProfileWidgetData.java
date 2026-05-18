package com.dashboard.model.widget;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder(toBuilder = true)
public class ProfileWidgetData {
    private String       widgetId;
    private String       userId;
    private String       name;
    private String       email;
    private String       avatarUrl;
    private String       role;
    private String       teamName;
    private List<String> permissions;
    private String       status;
    private Instant      fetchedAt;

    public static ProfileWidgetData fallback() {
        return ProfileWidgetData.builder()
                .widgetId("profile")
                .status("DEGRADED")
                .fetchedAt(Instant.now())
                .build();
    }
}
