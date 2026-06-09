# CompletableFuture API Endpoints - Testing Guide

## All Available Endpoints

### 1. **allOf() - Parallel Execution** (Original)
```bash
curl http://localhost:8080/api/aggregator/user/12345
```
**What it does:** Fetches all data (profile, orders, recommendations, rewards) in parallel using `CompletableFuture.allOf()`.

**CompletableFuture Method:** `allOf()`

**Use Case:** When you need all results and want to execute them in parallel.

---

### 2. **thenCombine() - Sequential Combination** (Original)
```bash
curl http://localhost:8080/api/aggregator/user/12345/combine
```
**What it does:** Combines multiple futures sequentially using `thenCombine()`.

**CompletableFuture Method:** `thenCombine()`

**Use Case:** When operations can run in parallel but results need to be combined step by step.

---

### 3. **Sequential Execution** (Original)
```bash
curl http://localhost:8080/api/aggregator/users/12345/sequential
```
**What it does:** Executes all calls sequentially (one after another) for performance comparison.

**CompletableFuture Method:** None (blocking calls)

**Use Case:** Baseline for comparing performance with parallel approaches.

---

### 4. **thenApply() - Synchronous Transformation** (NEW)
```bash
curl http://localhost:8080/api/aggregator/user/12345/then-apply
```
**What it does:** Fetches user profile and transforms the result synchronously in the same thread.

**CompletableFuture Method:** `thenApply()`

**Use Case:** Transform the result of a CompletableFuture synchronously without switching threads.

**Example Code:**
```java
CompletableFuture.supplyAsync(() -> fetchData())
    .thenApply(data -> transformData(data));
```

---

### 5. **thenApplyAsync() - Asynchronous Transformation** (NEW)
```bash
curl http://localhost:8080/api/aggregator/user/12345/then-apply-async
```
**What it does:** Fetches user profile and transforms the result asynchronously in a different thread from the executor pool.

**CompletableFuture Method:** `thenApplyAsync()`

**Use Case:** Transform the result asynchronously in a separate thread, useful for CPU-intensive transformations.

**Example Code:**
```java
CompletableFuture.supplyAsync(() -> fetchData())
    .thenApplyAsync(data -> expensiveTransformation(data), executorService);
```

---

### 6. **thenAcceptBoth() - Process Two Results** (NEW)
```bash
curl http://localhost:8080/api/aggregator/user/12345/then-accept-both
```
**What it does:** Fetches orders and rewards in parallel, then processes both when they complete.

**CompletableFuture Method:** `thenAcceptBoth()`

**Use Case:** Execute an action when two independent futures complete. Good for side effects like logging or notifications.

**Example Code:**
```java
ordersFuture.thenAcceptBoth(rewardsFuture, (orders, rewards) -> {
    log.info("Orders: {}, Rewards: {}", orders.size(), rewards.size());
});
```

---

### 7. **applyToEither() - Fastest of Two** (NEW)
```bash
curl http://localhost:8080/api/aggregator/user/12345/apply-to-either
```
**What it does:** Fetches recommendations from two sources and uses whichever completes first.

**CompletableFuture Method:** `applyToEither()`

**Use Case:** Race condition - use the fastest result between two competing sources. Great for redundancy and performance.

**Example Code:**
```java
source1Future.applyToEither(source2Future, result -> {
    return processResult(result);
});
```

---

### 8. **anyOf() - First Among Multiple** (NEW)
```bash
curl http://localhost:8080/api/aggregator/user/12345/any-of
```
**What it does:** Starts all four service calls and returns information about whichever completes first.

**CompletableFuture Method:** `anyOf()`

**Use Case:** Get the first completed result among multiple futures. Useful for health checks or identifying the fastest service.

**Example Code:**
```java
CompletableFuture<Object> firstCompleted = CompletableFuture.anyOf(
    future1, future2, future3, future4
);
Object result = firstCompleted.join();
```

---

## Quick Test Commands

### Test all new endpoints at once:
```bash
# Test thenApply
curl http://localhost:8080/api/aggregator/user/12345/then-apply

# Test thenApplyAsync
curl http://localhost:8080/api/aggregator/user/12345/then-apply-async

# Test thenAcceptBoth
curl http://localhost:8080/api/aggregator/user/12345/then-accept-both

# Test applyToEither
curl http://localhost:8080/api/aggregator/user/12345/apply-to-either

# Test anyOf
curl http://localhost:8080/api/aggregator/user/12345/any-of
```

### Compare Performance:
```bash
# Sequential (slowest)
time curl http://localhost:8080/api/aggregator/users/12345/sequential

# Parallel with allOf (fastest for getting all data)
time curl http://localhost:8080/api/aggregator/user/12345

# Parallel with thenCombine
time curl http://localhost:8080/api/aggregator/user/12345/combine
```

---

## CompletableFuture Methods Summary

| Method | Type | Thread Behavior | Use Case |
|--------|------|----------------|----------|
| `supplyAsync()` | Create | Async in executor | Start async computation |
| `allOf()` | Combine | Wait for all | All results needed |
| `anyOf()` | Combine | Wait for first | First result needed |
| `thenApply()` | Transform | Same thread | Quick transformation |
| `thenApplyAsync()` | Transform | Different thread | Heavy transformation |
| `thenCombine()` | Combine | When both complete | Combine two results |
| `thenAcceptBoth()` | Action | When both complete | Side effect on two results |
| `applyToEither()` | Race | Whichever first | Fastest of two sources |

---

## Response Status Messages

Each endpoint returns a unique status message to identify which CompletableFuture method was used:

- `"SUCCESS"` - allOf() endpoint
- `"SUCCESS - Transformed with thenApply"` - thenApply() endpoint
- `"SUCCESS - Transformed with thenApplyAsync"` - thenApplyAsync() endpoint
- `"SUCCESS - Processed with thenAcceptBoth"` - thenAcceptBoth() endpoint
- `"SUCCESS - Fastest result with applyToEither"` - applyToEither() endpoint
- `"SUCCESS - First completed: [ServiceName] (using anyOf)"` - anyOf() endpoint

---

## Running the Services

1. Start all microservices (User Profile, Order, Recommendation, Reward)
2. Start the Aggregator service
3. Test the endpoints using the curl commands above
4. Check logs to see the CompletableFuture execution patterns

---

## Key Differences

### thenApply vs thenApplyAsync
- **thenApply**: Runs transformation in the same thread (synchronous)
- **thenApplyAsync**: Runs transformation in executor thread pool (asynchronous)

### thenCombine vs thenAcceptBoth
- **thenCombine**: Combines two futures and returns a result
- **thenAcceptBoth**: Processes two futures but returns no result (void)

### applyToEither vs anyOf
- **applyToEither**: Race between exactly TWO futures
- **anyOf**: Race among ANY NUMBER of futures

---

## Best Practices

1. **Use `allOf()`** when you need all results
2. **Use `anyOf()`** when first result is sufficient
3. **Use `thenApply()`** for lightweight transformations
4. **Use `thenApplyAsync()`** for CPU-intensive transformations
5. **Use `thenAcceptBoth()`** for side effects (logging, notifications)
6. **Use `applyToEither()`** for redundancy and fallback scenarios

---

## Monitoring Tips

Check the logs to see:
- Thread names (to verify async execution)
- Response times for each method
- Which service completes first (for anyOf and applyToEither)

Example log output:
```
2026-05-11 10:30:15.123 INFO  - Starting data aggregation for userId: 12345
2026-05-11 10:30:15.124 INFO  - Async transformation on thread: pool-1-thread-2
2026-05-11 10:30:16.371 INFO  - First completed service: Orders completed first for userId: 12345
```
