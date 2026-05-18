package com.dashboard.service;

import com.dashboard.model.widget.ProfileWidgetData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class UserService {

    // Simulated user store (replace with JPA repository)
    private static final Map<String, ProfileWidgetData> USER_STORE = Map.of(
            "user-001", ProfileWidgetData.builder()
                    .userId("user-001").name("Alice Johnson").email("alice@company.com")
                    .avatarUrl("https://i.pravatar.cc/150?u=alice")
                    .role("ADMIN").teamName("Platform Engineering")
                    .permissions(List.of("READ", "WRITE", "ADMIN"))
                    .status("OK").build(),
            "user-002", ProfileWidgetData.builder()
                    .userId("user-002").name("Bob Smith").email("bob@company.com")
                    .avatarUrl("https://i.pravatar.cc/150?u=bob")
                    .role("DEVELOPER").teamName("Backend Team")
                    .permissions(List.of("READ", "WRITE"))
                    .status("OK").build()
    );

    /**
     * Fetches user profile. Cached for 5 minutes (profiles change rarely).
     */
    @Cacheable(value = "widget:profile", key = "#userId")
    public ProfileWidgetData getProfile(String userId) {
        log.debug("[UserService] Fetching profile for user={}", userId);
        simulateLatency(50, 100);

        ProfileWidgetData profile = USER_STORE.getOrDefault(userId,
                ProfileWidgetData.builder()
                        .userId(userId)
                        .name("Unknown User")
                        .email("unknown@company.com")
                        .role("VIEWER")
                        .teamName("N/A")
                        .permissions(List.of("READ"))
                        .status("OK")
                        .build());

        return profile.toBuilder()
                .widgetId("profile")
                .fetchedAt(Instant.now())
                .build();
    }

    private void simulateLatency(int minMs, int maxMs) {
        try {
            Thread.sleep(minMs + (long)(Math.random() * (maxMs - minMs)));
        } catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}
