# Aggregator API - Documentation

## Overview

This is a **production-ready aggregator API** built with Spring Boot that demonstrates the real-world use case of **calling multiple independent REST APIs in parallel** using Java **CompletableFuture** and combining their responses.

### Key Benefits

✅ **Reduced Response Time**: Parallel API calls instead of sequential
✅ **Resilient**: Fallback mechanisms for failed services
✅ **Scalable**: Custom thread pool configuration
✅ **Production-Ready**: Proper error handling, logging, and monitoring

---

## Architecture

### Microservices Integration

The aggregator calls 4 independent microservices in parallel:

1. **User Profile Service** - User information (name, email, membership)
2. **Orders Service** - User's order history
3. **Recommendations Service** - Personalized product recommendations
4. **Rewards Service** - User's reward points and tier

### Parallel Execution Flow

```
Client Request (userId: "123")
        |
        v
Aggregator API
        |
        +------------------+------------------+------------------+
        |                  |                  |                  |
        v                  v                  v                  v
  User Profile       Orders API      Recommendations     Rewards API
    Service                              Service            Service
        |                  |                  |                  |
        +------------------+------------------+------------------+
                           |
                           v
                  Combine Responses
                           |
                           v
                   Return Aggregated Data
```

**Sequential Time**: ~5000ms (5 seconds)
**Parallel Time**: ~1250ms (1.25 seconds) ⚡

---

## API Endpoints

### 1. Get Aggregated User Data (Main Endpoint)

**GET** `/api/aggregator/user/{userId}`

Fetches data from all 4 microservices in parallel and returns aggregated response.

**Example Request:**
```bash
curl http://localhost:8080/api/aggregator/user/12345
```

**Example Response:**
```json
{
  "userId": "12345",
  "profile": {
    "userId": "12345",
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "+1-234-567-8900",
    "address": "123 Main St, New York, NY",
    "membershipLevel": "GOLD"
  },
  "orders": {
    "orders": [
      {
        "orderId": "ORD-001",
        "userId": "12345",
        "productName": "Laptop",
        "amount": 1299.99,
        "status": "DELIVERED",
        "orderDate": "2026-04-15T10:30:00"
      }
    ],
    "totalOrders": 5
  },
  "recommendations": {
    "recommendations": [
      {
        "productId": "PROD-123",
        "productName": "Wireless Mouse",
        "category": "Electronics",
        "rating": 4.5,
        "reason": "Based on your recent purchases"
      }
    ]
  },
  "rewards": {
    "totalPoints": 2500,
    "tier": "GOLD",
    "rewards": [
      {
        "rewardId": "REW-001",
        "type": "CASHBACK",
        "points": 500,
        "description": "5% cashback on next purchase",
        "isActive": true
      }
    ]
  },
  "responseTimeMs": 1247,
  "status": "SUCCESS"
}
```

### 2. Get Aggregated User Data (Combine Approach)

**GET** `/api/aggregator/user/{userId}/combine`

Alternative implementation using `thenCombine()` for dependent operations.

**Example Request:**
```bash
curl http://localhost:8080/api/aggregator/user/12345/combine
```

### 3. Sequential Execution

**GET** `/api/aggregator/users/{userId}/sequential`

Sequential execution for comparison with parallel approaches.

**Example Request:**
```bash
curl http://localhost:8080/api/aggregator/users/12345/sequential
```

### 4. thenApply - Synchronous Transformation

**GET** `/api/aggregator/user/{userId}/then-apply`

Demonstrates `thenApply()` - transforms the result synchronously after fetching user profile.

**Example Request:**
```bash
curl http://localhost:8080/api/aggregator/user/12345/then-apply
```

**Key Feature:** Synchronous transformation in the same thread.

### 5. thenApplyAsync - Asynchronous Transformation

**GET** `/api/aggregator/user/{userId}/then-apply-async`

Demonstrates `thenApplyAsync()` - transforms the result asynchronously in a different thread.

**Example Request:**
```bash
curl http://localhost:8080/api/aggregator/user/12345/then-apply-async
```

**Key Feature:** Asynchronous transformation in executor thread pool.

### 6. thenAcceptBoth - Process Two Futures Together

**GET** `/api/aggregator/user/{userId}/then-accept-both`

Demonstrates `thenAcceptBoth()` - processes results from two futures when both complete.

**Example Request:**
```bash
curl http://localhost:8080/api/aggregator/user/12345/then-accept-both
```

**Key Feature:** Performs an action when both orders and rewards futures complete.

### 7. applyToEither - First Completed Result

**GET** `/api/aggregator/user/{userId}/apply-to-either`

Demonstrates `applyToEither()` - uses whichever future completes first among two sources.

**Example Request:**
```bash
curl http://localhost:8080/api/aggregator/user/12345/apply-to-either
```

**Key Feature:** Returns the fastest result between two competing sources.

### 8. anyOf - First Among Multiple

**GET** `/api/aggregator/user/{userId}/any-of`

Demonstrates `anyOf()` - returns the first completed future among multiple services.

**Example Request:**
```bash
curl http://localhost:8080/api/aggregator/user/12345/any-of
```

**Key Feature:** Identifies which service responds fastest.

### 9. Health Check

**GET** `/api/aggregator/health`

**Response:**
```json
"Aggregator API is running!"
```

---

## Configuration

### Application Properties

Edit `src/main/resources/application.yml`:

```yaml
microservices:
  user-profile:
    url: http://localhost:8081/api/users
    timeout: 5000
  orders:
    url: http://localhost:8082/api/orders
    timeout: 5000
  recommendations:
    url: http://localhost:8083/api/recommendations
    timeout: 5000
  rewards:
    url: http://localhost:8084/api/rewards
    timeout: 5000

async:
  core-pool-size: 10
  max-pool-size: 50
  queue-capacity: 100
```

---

## Running the Application

### Prerequisites
- Java 17+
- Maven 3.6+

### Build and Run

```bash
# Build
mvn clean install

# Run
mvn spring-boot:run
```

The application will start on **http://localhost:8080**

---

## Testing

### With Mock Services

Since the actual microservices might not be running, the application includes **fallback mechanisms** that return default data when services are unavailable.

**Test the aggregator:**
```bash
curl http://localhost:8080/api/aggregator/user/test-user-123
```

You'll get a response with fallback data and still see the parallel execution pattern in logs.

### With WireMock (Recommended)

Create mock services using WireMock or similar tools:

```bash
# Example: Start mock services on different ports
# User Profile: 8081
# Orders: 8082
# Recommendations: 8083
# Rewards: 8084
```

---

## CompletableFuture Implementation Details

### Approach 1: Using `allOf()` (Recommended for Independent Operations)

```java
// Create independent futures
CompletableFuture<UserProfile> profileFuture =
    CompletableFuture.supplyAsync(() -> userProfileClient.getUserProfile(userId), executorService);

CompletableFuture<OrderResponse> ordersFuture =
    CompletableFuture.supplyAsync(() -> orderClient.getUserOrders(userId), executorService);

// Wait for all to complete
CompletableFuture.allOf(profileFuture, ordersFuture, ...).join();

// Get results
UserProfile profile = profileFuture.join();
OrderResponse orders = ordersFuture.join();
```

**Benefits:**
- All APIs called in parallel
- Independent error handling
- Clear and readable

### Approach 2: Using `thenCombine()`

```java
CompletableFuture<AggregatedUserData> result = profileFuture
    .thenCombine(ordersFuture, (profile, orders) -> ...)
    .thenCombine(rewardsFuture, (aggregated, rewards) -> ...);
```

**Benefits:**
- Functional composition
- Good for dependent operations
- Cleaner for chaining

### Approach 3: Using `thenApply()` and `thenApplyAsync()`

```java
// thenApply - synchronous transformation
CompletableFuture<AggregatedUserData> result = CompletableFuture
    .supplyAsync(() -> userProfileClient.getUserProfile(userId))
    .thenApply(profile -> transformToAggregatedData(profile));

// thenApplyAsync - asynchronous transformation in executor
CompletableFuture<AggregatedUserData> result = CompletableFuture
    .supplyAsync(() -> userProfileClient.getUserProfile(userId))
    .thenApplyAsync(profile -> transformToAggregatedData(profile), executorService);
```

**Benefits:**
- Transform results without blocking
- Async variant uses custom executor
- Chain multiple transformations

### Approach 4: Using `thenAcceptBoth()`

```java
CompletableFuture<Void> result = ordersFuture
    .thenAcceptBoth(rewardsFuture, (orders, rewards) -> {
        // Process both results together
        log.info("Orders: {}, Rewards: {}", orders.size(), rewards.size());
    });
```

**Benefits:**
- Process two results together when both complete
- Good for side effects (logging, notifications)
- No return value needed

### Approach 5: Using `applyToEither()`

```java
CompletableFuture<RecommendationResponse> result = source1Future
    .applyToEither(source2Future, recommendations -> {
        // Use whichever completes first
        return processRecommendations(recommendations);
    });
```

**Benefits:**
- Race condition - fastest wins
- Redundancy for critical operations
- Improved response time

### Approach 6: Using `anyOf()`

```java
CompletableFuture<Object> firstCompleted = CompletableFuture.anyOf(
    profileFuture, ordersFuture, recommendationsFuture, rewardsFuture
);

Object result = firstCompleted.join();
```

**Benefits:**
- Get first completed among multiple futures
- Useful for monitoring/health checks
- Identify fastest service

---

## Performance Comparison

### Sequential Execution
```
UserProfile:     1200ms
Orders:          1000ms
Recommendations:  800ms
Rewards:          500ms
--------------------------
Total:           3500ms
```

### Parallel Execution (CompletableFuture)
```
All 4 APIs run simultaneously
--------------------------
Total: ~1200ms (longest API)
Speedup: 2.9x faster ⚡
```

---

## Error Handling

### Fallback Mechanism

Each client has a fallback method that returns default data if the service fails:

```java
.onErrorResume(error -> {
    log.error("Error fetching user profile: {}", error.getMessage());
    return Mono.just(createFallbackUserProfile(userId));
})
```

### Partial Failure Handling

If some services fail, the aggregator still returns data from successful calls with status `PARTIAL_FAILURE`.

---

## Monitoring

### Metrics Available

- Response time per request
- Success/failure rates
- Individual service response times

### Logs

```
2026-05-08 10:30:15.123 INFO  - Starting data aggregation for userId: 12345
2026-05-08 10:30:15.124 DEBUG - Fetching user profile for userId: 12345
2026-05-08 10:30:15.124 DEBUG - Fetching orders for userId: 12345
2026-05-08 10:30:15.124 DEBUG - Fetching recommendations for userId: 12345
2026-05-08 10:30:15.124 DEBUG - Fetching rewards for userId: 12345
2026-05-08 10:30:16.371 INFO  - Data aggregation completed for userId: 12345 in 1247ms
```

---

## Production Considerations

✅ **Implemented:**
- Custom thread pool for better control
- Timeouts on all external calls
- Fallback mechanisms
- Proper error handling
- Detailed logging
- WebClient with connection pooling

⚠️ **Consider Adding:**
- Circuit breaker (Resilience4j)
- Distributed tracing (Sleuth/Zipkin)
- Metrics (Micrometer/Prometheus)
- Caching (Redis)
- Rate limiting
- API authentication

---

## Project Structure

```
aggregator-api/
├── src/main/java/com/epam/practice/aggregator/
│   ├── AggregatorApiApplication.java      # Main application
│   ├── config/
│   │   ├── AsyncConfig.java               # Thread pool config
│   │   └── WebClientConfig.java           # HTTP client config
│   ├── controller/
│   │   └── AggregatorController.java      # REST endpoints
│   ├── dto/
│   │   ├── UserProfile.java
│   │   ├── Order.java
│   │   ├── OrderResponse.java
│   │   ├── Recommendation.java
│   │   ├── RecommendationResponse.java
│   │   ├── Reward.java
│   │   ├── RewardResponse.java
│   │   └── AggregatedUserData.java        # Combined response
│   ├── service/
│   │   └── AggregatorService.java         # CompletableFuture logic
│   ├── client/
│   │   ├── UserProfileClient.java
│   │   ├── OrderClient.java
│   │   ├── RecommendationClient.java
│   │   └── RewardClient.java
│   └── exception/
│       └── ServiceException.java
└── src/main/resources/
    └── application.yml                     # Configuration
```

---

## Key Takeaways

1. **CompletableFuture** enables parallel execution, significantly reducing response time
2. **Fallback mechanisms** ensure resilience even when services fail
3. **Custom thread pools** provide better control over concurrency
4. **WebClient** with proper timeouts prevents hanging requests
5. **Proper error handling** ensures graceful degradation

---

## License

MIT
