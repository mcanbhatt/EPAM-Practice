package com.dashboard.service;

import com.dashboard.model.response.DashboardResponse;
import com.dashboard.model.widget.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

/**
 * ╔══════════════════════════════════════════════════════════════╗
 * ║           DASHBOARD ORCHESTRATOR                             ║
 * ║                                                              ║
 * ║  Fans out all 6 widget fetches in PARALLEL using             ║
 * ║  CompletableFuture.  Total latency = slowest widget,         ║
 * ║  not the sum of all widgets.                                 ║
 * ║                                                              ║
 * ║  CompletableFuture features used:                            ║
 * ║   • supplyAsync(supplier, executor)  – offload to pool       ║
 * ║   • orTimeout(n, unit)               – per-widget SLA        ║
 * ║   • exceptionally(fn)                – graceful fallback     ║
 * ║   • thenApply(fn)                    – transform result      ║
 * ║   • thenCompose(fn)                  – chain dependents      ║
 * ║   • thenCombine(future, fn)          – merge two results     ║
 * ║   • allOf(...).join()                – fan-in barrier        ║
 * ╚══════════════════════════════════════════════════════════════╝
 */
@Service
@Slf4j
@RequiredArgsConstructor
public class DashboardOrchestrator {

    private final AnalyticsService     analyticsService;
    private final UserService          userService;
    private final NotificationService  notificationService;
    private final FinanceService       financeService;
    private final InventoryService     inventoryService;
    private final TaskService          taskService;

    @Qualifier("dashboardWidgetExecutor")
    private final Executor widgetExecutor;

    @Value("${dashboard.widget.timeout-seconds:3}")
    private long timeoutSeconds;

    // ─────────────────────────────────────────────────────────────────────
    //  Main entry point
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Baseline method: builds the dashboard by loading all widgets SEQUENTIALLY.
     * @param userId
     * @return
     */
    public DashboardResponse sequentially(String userId) {
		long start = System.currentTimeMillis();
		log.info("[Orchestrator] Building dashboard SEQUENTIALLY for userId={}", userId);

		ChartWidgetData   charts  = analyticsService.getCharts(userId);
		ProfileWidgetData profile = userService.getProfile(userId);
		AlertWidgetData   alerts  = notificationService.getAlerts(userId);
		RevenueWidgetData revenue = financeService.getRevenue(userId);
		StockWidgetData   stock   = inventoryService.getStock(userId);
		TaskWidgetData    tasks   = taskService.getTasks(userId);

		long loadTimeMs = System.currentTimeMillis() - start;
		log.info("[Orchestrator] Sequential dashboard built in {}ms for userId={}", loadTimeMs, userId);

		return DashboardResponse.builder()
				.charts(charts)
				.profile(profile)
				.alerts(alerts)
				.revenue(revenue)
				.stock(stock)
				.tasks(tasks)
				.generatedAt(Instant.now())
				.loadTimeMs(loadTimeMs)
				.build();
	}
    /**
     * Builds the full dashboard by loading all widgets in PARALLEL.
     *
     * @param userId the authenticated user's ID
     * @return aggregated DashboardResponse
     */
    public DashboardResponse buildDashboard(String userId) {
        long start = System.currentTimeMillis();
        log.info("[Orchestrator] Building dashboard for userId={}", userId);

        // ── 1. Fan-out: kick off all 6 widget futures simultaneously ──────
        //    Each runs on a separate thread from dashboardWidgetExecutor.
        //    They do NOT wait for each other at this point.

        CompletableFuture<ChartWidgetData> chartsFuture =
                CompletableFuture
                        .supplyAsync(() -> analyticsService.getCharts(userId), widgetExecutor)
                        .orTimeout(3, TimeUnit.MILLISECONDS)
                        .exceptionally(ex -> {
                            return ChartWidgetData.fallback();
                        });

        CompletableFuture<ProfileWidgetData> profileFuture =
                CompletableFuture
                        .supplyAsync(() -> userService.getProfile(userId), widgetExecutor)
                        .orTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .exceptionally(ex -> {
                            log.error("[Orchestrator] Profile widget failed: {}", ex.getMessage());
                            return ProfileWidgetData.fallback();
                        });

        CompletableFuture<AlertWidgetData> alertsFuture =
                CompletableFuture
                        .supplyAsync(() -> notificationService.getAlerts(userId), widgetExecutor)
                        .orTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .exceptionally(ex -> {
                            log.error("[Orchestrator] Alerts widget failed: {}", ex.getMessage());
                            return AlertWidgetData.fallback();
                        });

        CompletableFuture<RevenueWidgetData> revenueFuture =
                CompletableFuture
                        .supplyAsync(() -> financeService.getRevenue(userId), widgetExecutor)
                        .orTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .exceptionally(ex -> {
                            log.error("[Orchestrator] Revenue widget failed: {}", ex.getMessage());
                            return RevenueWidgetData.fallback();
                        });

        CompletableFuture<StockWidgetData> stockFuture =
                CompletableFuture
                        .supplyAsync(() -> inventoryService.getStock(userId), widgetExecutor)
                        .orTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .exceptionally(ex -> {
                            log.error("[Orchestrator] Stock widget failed: {}", ex.getMessage());
                            return StockWidgetData.fallback();
                        });

        CompletableFuture<TaskWidgetData> tasksFuture =
                CompletableFuture
                        .supplyAsync(() -> taskService.getTasks(userId), widgetExecutor)
                        .orTimeout(timeoutSeconds, TimeUnit.SECONDS)
                        .exceptionally(ex -> {
                            log.error("[Orchestrator] Tasks widget failed: {}", ex.getMessage());
                            return TaskWidgetData.fallback();
                        });

        // ── 2. Fan-in barrier: wait for ALL futures to complete ────────────
        //    Total wait = MAX(all widget latencies), NOT the sum.
        //    Example: if widgets take 120, 80, 60, 200, 150, 90 ms
        //             sequential = 700ms  |  parallel = 200ms  ← winner
        CompletableFuture.allOf(
                chartsFuture,
                profileFuture,
                alertsFuture,
                revenueFuture,
                stockFuture,
                tasksFuture
        ).join();  // blocks the calling thread here only

        // ── 3. Collect results (all futures already done at this point) ────
        ChartWidgetData   charts  = chartsFuture .join();
        ProfileWidgetData profile = profileFuture.join();
        AlertWidgetData   alerts  = alertsFuture .join();
        RevenueWidgetData revenue = revenueFuture.join();
        StockWidgetData   stock   = stockFuture  .join();
        TaskWidgetData    tasks   = tasksFuture  .join();

        // ── 4. Build per-widget status summary ────────────────────────────
        Map<String, String> widgetStatuses = new HashMap<>();
        widgetStatuses.put("charts",  charts .getStatus());
        widgetStatuses.put("profile", profile.getStatus());
        widgetStatuses.put("alerts",  alerts .getStatus());
        widgetStatuses.put("revenue", revenue.getStatus());
        widgetStatuses.put("stock",   stock  .getStatus());
        widgetStatuses.put("tasks",   tasks  .getStatus());

        long loadTimeMs = System.currentTimeMillis() - start;
        log.info("[Orchestrator] Dashboard built in {}ms for userId={}", loadTimeMs, userId);

        return DashboardResponse.builder()
                .charts(charts)
                .profile(profile)
                .alerts(alerts)
                .revenue(revenue)
                .stock(stock)
                .tasks(tasks)
                .generatedAt(Instant.now())
                .loadTimeMs(loadTimeMs)
                .widgetStatuses(widgetStatuses)
                .build();
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Advanced: thenCompose example — profile enriched with team details
    //  (demonstrates chaining dependent CompletableFutures)
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Example of thenCompose: enriches the profile with team details
     * only AFTER the base profile has been fetched. The team fetch
     * depends on the teamName returned by getProfile().
     */
    public CompletableFuture<ProfileWidgetData> getEnrichedProfile(String userId) {
        return CompletableFuture
                .supplyAsync(() -> userService.getProfile(userId), widgetExecutor)
                .thenCompose(profile -> {
                    // Only called when profile is ready — dependent chain
                    return CompletableFuture.supplyAsync(() -> {
                        // Simulate enriching profile with team membership details
                        log.debug("[Orchestrator] Enriching profile with team data for team={}",profile.getTeamName());
                        return profile.toBuilder()
                                .teamName(profile.getTeamName() + " (enriched)")
                                .build();
                    }, widgetExecutor);
                })
                .orTimeout(timeoutSeconds, TimeUnit.SECONDS)
                .exceptionally(ex -> ProfileWidgetData.fallback());
    }

    // ─────────────────────────────────────────────────────────────────────
    //  Advanced: thenCombine example — merges revenue + stock into summary
    //  (demonstrates combining two INDEPENDENT futures)
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Example of thenCombine: merges results of two independent futures
     * into a single combined Map. Both futures run in parallel; the
     * combiner only runs when BOTH are done.
     */
    public CompletableFuture<Map<String, Object>> getFinancialSummary(String userId) {
        CompletableFuture<RevenueWidgetData> revFuture =
                CompletableFuture.supplyAsync(() -> financeService.getRevenue(userId), widgetExecutor);

        CompletableFuture<StockWidgetData> stockFuture2 =
                CompletableFuture.supplyAsync(() -> inventoryService.getStock(userId), widgetExecutor);

        return revFuture.thenCombine(stockFuture2, (rev, stk) -> {
            Map<String, Object> summary = new HashMap<>();
            summary.put("totalRevenue",    rev.getTotalRevenue());
            summary.put("revenueDelta",    rev.getDelta());
            summary.put("totalStockItems", stk.getTotalItems());
            summary.put("lowStockItems",   stk.getLowStockCount());
            summary.put("outOfStock",      stk.getOutOfStockCount());
            return summary;
        });
    }
}
