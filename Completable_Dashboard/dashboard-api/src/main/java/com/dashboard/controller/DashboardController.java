package com.dashboard.controller;

import com.dashboard.model.response.DashboardResponse;
import com.dashboard.model.widget.ProfileWidgetData;
import com.dashboard.service.DashboardOrchestrator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Dashboard API", description = "Parallel widget loading via CompletableFuture")
public class DashboardController {

    private final DashboardOrchestrator orchestrator;

    // ─────────────────────────────────────────────────────────────────────
    //  GET /api/v1/dashboard/{userId}
    //  Main endpoint — returns all 6 widgets loaded in parallel
    // ─────────────────────────────────────────────────────────────────────

    @GetMapping("/{userId}")
    @Operation(
        summary     = "Get full dashboard",
        description = "Loads all 6 widgets in parallel using CompletableFuture.allOf(). "
                    + "Total latency equals the slowest widget, not the sum."
    )
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Dashboard loaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid user ID"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<DashboardResponse> getDashboard(
            @PathVariable
            @NotBlank
            @Parameter(description = "User ID", example = "user-001")
            String userId) {

        log.info("GET /api/v1/dashboard/{}", userId);
        DashboardResponse response = orchestrator.buildDashboard(userId);
        return ResponseEntity.ok(response);
    }
    
    
    
    @GetMapping("/{userId}/sequential")
    @Operation(
        summary     = "Get full dashboard with sequential loading (for comparison)",
        description = "Total latency equals to the sum."
    )
    public ResponseEntity<DashboardResponse> getDashboardsequentially(
            @PathVariable
            @NotBlank
            @Parameter(description = "User ID", example = "user-001")
            String userId) {

        log.info("GET /api/v1/dashboard/{}", userId);
        DashboardResponse response = orchestrator.sequentially(userId);
        return ResponseEntity.ok(response);
    }

    // ─────────────────────────────────────────────────────────────────────
    //  GET /api/v1/dashboard/{userId}/profile/enriched
    //  Demonstrates thenCompose — dependent chaining
    // ─────────────────────────────────────────────────────────────────────

    @GetMapping("/{userId}/profile/enriched")
    @Operation(
        summary     = "Get enriched user profile",
        description = "Demonstrates CompletableFuture.thenCompose() for dependent async calls."
    )
    public ResponseEntity<ProfileWidgetData> getEnrichedProfile(@PathVariable String userId)
            throws Exception {
        ProfileWidgetData profile = orchestrator.getEnrichedProfile(userId).get();
        return ResponseEntity.ok(profile);
    }
    
    

    // ─────────────────────────────────────────────────────────────────────
    //  GET /api/v1/dashboard/{userId}/financial-summary
    //  Demonstrates thenCombine — merging two independent futures
    // ─────────────────────────────────────────────────────────────────────

    @GetMapping("/{userId}/financial-summary")
    @Operation(
        summary     = "Get financial summary",
        description = "Demonstrates CompletableFuture.thenCombine() — merges revenue and stock futures."
    )
    public ResponseEntity<Map<String, Object>> getFinancialSummary(@PathVariable String userId)
            throws Exception {
        Map<String, Object> summary = orchestrator.getFinancialSummary(userId).get();
        return ResponseEntity.ok(summary);
    }

    // ─────────────────────────────────────────────────────────────────────
    //  GET /api/v1/dashboard/health
    // ─────────────────────────────────────────────────────────────────────

    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Simple liveness check for the dashboard API")
    public ResponseEntity<Map<String, String>> health() {
        return ResponseEntity.ok(Map.of(
                "status",  "UP",
                "service", "dashboard-api",
                "version", "1.0.0"
        ));
    }
}
