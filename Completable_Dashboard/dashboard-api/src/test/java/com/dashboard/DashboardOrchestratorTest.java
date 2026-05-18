package com.dashboard;

import com.dashboard.model.response.DashboardResponse;
import com.dashboard.model.widget.*;
import com.dashboard.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DashboardOrchestratorTest {

    @Mock private AnalyticsService    analyticsService;
    @Mock private UserService         userService;
    @Mock private NotificationService notificationService;
    @Mock private FinanceService      financeService;
    @Mock private InventoryService    inventoryService;
    @Mock private TaskService         taskService;

    private DashboardOrchestrator orchestrator;

    @BeforeEach
    void setUp() {
        orchestrator = new DashboardOrchestrator(
                analyticsService, userService, notificationService,
                financeService, inventoryService, taskService,
                Executors.newFixedThreadPool(6)
        );
        // Set timeout via reflection for tests
        try {
            var f = DashboardOrchestrator.class.getDeclaredField("timeoutSeconds");
            f.setAccessible(true);
            f.set(orchestrator, 5L);
        } catch (Exception ignored) {}
    }

    @Test
    void buildDashboard_allServicesOk_returnsFullResponse() {
        // Arrange
        when(analyticsService.getCharts(anyString()))
                .thenReturn(ChartWidgetData.builder().widgetId("charts")
                        .status("OK").series(List.of()).fetchedAt(Instant.now()).build());
        when(userService.getProfile(anyString()))
                .thenReturn(ProfileWidgetData.builder().widgetId("profile")
                        .status("OK").fetchedAt(Instant.now()).build());
        when(notificationService.getAlerts(anyString()))
                .thenReturn(AlertWidgetData.builder().widgetId("alerts")
                        .status("OK").alerts(List.of()).fetchedAt(Instant.now()).build());
        when(financeService.getRevenue(anyString()))
                .thenReturn(RevenueWidgetData.builder().widgetId("revenue")
                        .status("OK").totalRevenue(BigDecimal.TEN).currency("USD")
                        .breakdown(List.of()).fetchedAt(Instant.now()).build());
        when(inventoryService.getStock(anyString()))
                .thenReturn(StockWidgetData.builder().widgetId("stock")
                        .status("OK").items(List.of()).fetchedAt(Instant.now()).build());
        when(taskService.getTasks(anyString()))
                .thenReturn(TaskWidgetData.builder().widgetId("tasks")
                        .status("OK").tasks(List.of()).fetchedAt(Instant.now()).build());

        // Act
        DashboardResponse response = orchestrator.buildDashboard("user-001");

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getCharts().getStatus()).isEqualTo("OK");
        assertThat(response.getProfile().getStatus()).isEqualTo("OK");
        assertThat(response.getAlerts().getStatus()).isEqualTo("OK");
        assertThat(response.getRevenue().getStatus()).isEqualTo("OK");
        assertThat(response.getStock().getStatus()).isEqualTo("OK");
        assertThat(response.getTasks().getStatus()).isEqualTo("OK");
        assertThat(response.getWidgetStatuses()).hasSize(6);
        assertThat(response.getLoadTimeMs()).isGreaterThanOrEqualTo(0);

        // All 6 services must be called exactly once
        verify(analyticsService,    times(1)).getCharts(anyString());
        verify(userService,         times(1)).getProfile(anyString());
        verify(notificationService, times(1)).getAlerts(anyString());
        verify(financeService,      times(1)).getRevenue(anyString());
        verify(inventoryService,    times(1)).getStock(anyString());
        verify(taskService,         times(1)).getTasks(anyString());
    }

    @Test
    void buildDashboard_oneServiceFails_returnsFallbackForThatWidget() {
        // Arrange — FinanceService throws; all others succeed
        when(analyticsService.getCharts(anyString()))
                .thenReturn(ChartWidgetData.builder().widgetId("charts")
                        .status("OK").series(List.of()).fetchedAt(Instant.now()).build());
        when(userService.getProfile(anyString()))
                .thenReturn(ProfileWidgetData.builder().widgetId("profile")
                        .status("OK").fetchedAt(Instant.now()).build());
        when(notificationService.getAlerts(anyString()))
                .thenReturn(AlertWidgetData.builder().widgetId("alerts")
                        .status("OK").alerts(List.of()).fetchedAt(Instant.now()).build());
        when(financeService.getRevenue(anyString()))
                .thenThrow(new RuntimeException("Finance DB connection lost"));
        when(inventoryService.getStock(anyString()))
                .thenReturn(StockWidgetData.builder().widgetId("stock")
                        .status("OK").items(List.of()).fetchedAt(Instant.now()).build());
        when(taskService.getTasks(anyString()))
                .thenReturn(TaskWidgetData.builder().widgetId("tasks")
                        .status("OK").tasks(List.of()).fetchedAt(Instant.now()).build());

        // Act
        DashboardResponse response = orchestrator.buildDashboard("user-001");

        // Assert — other widgets unaffected; revenue is degraded
        assertThat(response.getCharts().getStatus()).isEqualTo("OK");
        assertThat(response.getRevenue().getStatus()).isEqualTo("DEGRADED");
        assertThat(response.getRevenue().getTotalRevenue()).isEqualTo(BigDecimal.ZERO);
        assertThat(response.getTasks().getStatus()).isEqualTo("OK");
    }
}
