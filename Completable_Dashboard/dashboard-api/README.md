# Dashboard API — Parallel Widget Loading with CompletableFuture

Spring Boot microservice that loads all dashboard widgets **in parallel** using `CompletableFuture`, reducing total latency to the **slowest widget** instead of the **sum of all widgets**.

---

## Architecture

```
Client → API Gateway → DashboardOrchestrator
                              │
              ┌───────────────┼──────────────────────────┐  
              ▼               ▼             ▼             ▼
        AnalyticsService  UserService  FinanceService  ...3 more
        (Charts Widget)  (Profile)    (Revenue)
              │               │             │
              └───────────────┴─────────────┘
                     CompletableFuture.allOf().join()
                     Total = MAX(latencies), not SUM
```

---

## Endpoints

| Method | Path | Description |
|--------|------|-------------|
| GET | `/api/v1/dashboard/{userId}` | Full dashboard — all 6 widgets in parallel |
| GET | `/api/v1/dashboard/{userId}/profile/enriched` | `thenCompose` demo: dependent chaining |
| GET | `/api/v1/dashboard/{userId}/financial-summary` | `thenCombine` demo: merge two futures |
| GET | `/api/v1/dashboard/health` | Health check |

Swagger UI: **http://localhost:8080/swagger-ui.html**  
H2 Console:  **http://localhost:8080/h2-console**

---

## CompletableFuture Features Used

| Feature | Where |
|---------|-------|
| `supplyAsync(s, executor)` | Fan-out each widget to the thread pool |
| `allOf(...).join()` | Fan-in barrier — wait for all 6 widgets |
| `orTimeout(n, unit)` | Per-widget SLA (default 3s) |
| `exceptionally(fn)` | Graceful fallback if a widget fails |
| `thenCompose(fn)` | Chain dependent calls (profile → team enrichment) |
| `thenCombine(f, fn)` | Merge two independent futures (revenue + stock) |
| `anyOf(...)` | Race pattern available in `FutureUtils` |
| `thenApply(fn)` | Transform result inline |

---

## Configuration

Key properties in `application.yml`:

```yaml
dashboard:
  thread-pool:
    core-size: 10          # threads for widget executor
    max-size: 20
  widget:
    timeout-seconds: 3     # per-widget CompletableFuture timeout
```

---

## Widget Services

| Widget | Service | Cache TTL | Simulated Latency |
|--------|---------|-----------|-------------------|
| Charts | AnalyticsService | 60s | 100–200ms |
| Profile | UserService | 300s | 50–100ms |
| Alerts | NotificationService | 30s | 40–90ms |
| Revenue | FinanceService | 120s | 150–250ms ← slowest |
| Stock | InventoryService | 60s | 80–150ms |
| Tasks | TaskService | 45s | 60–120ms |

**Sequential total:** ~700ms  
**Parallel total (this API):** ~250ms  ← 3× faster

---

## Project Structure

```
src/
├── main/java/com/dashboard/
│   ├── DashboardApiApplication.java
│   ├── config/
│   │   ├── AsyncConfig.java        ← Thread pool for CompletableFuture
│   │   ├── CacheConfig.java        ← Redis + per-widget TTLs
│   │   └── OpenApiConfig.java
│   ├── controller/
│   │   └── DashboardController.java
│   ├── service/
│   │   ├── DashboardOrchestrator.java  ← CompletableFuture.allOf() lives here
│   │   ├── AnalyticsService.java
│   │   ├── UserService.java
│   │   ├── NotificationService.java
│   │   ├── FinanceService.java
│   │   ├── InventoryService.java
│   │   └── TaskService.java
│   ├── model/
│   │   ├── widget/                 ← DTOs per widget
│   │   └── response/               ← DashboardResponse, ErrorResponse
│   ├── exception/
│   │   ├── WidgetFetchException.java
│   │   └── GlobalExceptionHandler.java
│   └── util/
│       └── FutureUtils.java        ← Reusable CF helpers
└── test/java/com/dashboard/
    ├── DashboardOrchestratorTest.java
    └── DashboardControllerIntegrationTest.java
```



## Running Locally

### Prerequisites
- Java 17+
- Maven 3.8+
- Redis (optional — app falls back to in-memory cache)

### Start the app
```bash
mvn spring-boot:run
```

### Run tests
```bash
mvn test
```

### Build fat JAR
```bash
mvn clean package
java -jar target/dashboard-api-1.0.0.jar
```

---