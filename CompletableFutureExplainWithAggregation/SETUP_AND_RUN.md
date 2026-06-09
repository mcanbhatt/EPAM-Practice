# Complete Setup and Run Guide

## Overview

This is a complete microservices ecosystem demonstrating **CompletableFuture** for parallel API calls:

- **4 Microservices**: User Profile, Orders, Recommendations, Rewards
- **1 Aggregator API**: Calls all 4 services in parallel
- **H2 Database**: Each service has prepopulated data
- **No Reactive Programming**: Uses RestTemplate instead of WebClient

---

## Project Structure

```
CompletableFuture/
├── aggregator-api/                    # Main aggregator (port 8080)
├── user-profile-service/              # Port 8081
├── order-service/                     # Port 8082
├── recommendation-service/            # Port 8083
└── reward-service/                    # Port 8084
```

---

## Step 1: Start All Microservices

Open **5 separate terminals** and run each service:

### Terminal 1: User Profile Service (Port 8081)
```bash
cd user-profile-service
mvn spring-boot:run
```

### Terminal 2: Order Service (Port 8082)
```bash
cd order-service
mvn spring-boot:run
```

### Terminal 3: Recommendation Service (Port 8083)
```bash
cd recommendation-service
mvn spring-boot:run
```

### Terminal 4: Reward Service (Port 8084)
```bash
cd reward-service
mvn spring-boot:run
```

### Terminal 5: Aggregator API (Port 8080)
```bash
cd /c/Users/NaveenBhatt/Project/EpamPractice/CompletableFuture
mvn spring-boot:run
```

---

## Step 2: Verify All Services Are Running

Check health endpoints:

```bash
# User Profile Service
curl http://localhost:8081/api/users/health

# Order Service
curl http://localhost:8082/api/orders/health

# Recommendation Service
curl http://localhost:8083/api/recommendations/health

# Reward Service
curl http://localhost:8084/api/rewards/health

# Aggregator API
curl http://localhost:8080/api/aggregator/health
```

All should respond with success messages!

---

## Step 3: Test Individual Services

### Test User Profile Service
```bash
curl http://localhost:8081/api/users/user001
```

**Response:**
```json
{
  "userId": "user001",
  "name": "John Doe",
  "email": "john.doe@example.com",
  "phone": "+1-555-0101",
  "address": "123 Main St, New York, NY 10001",
  "membershipLevel": "GOLD"
}
```

### Test Order Service
```bash
curl http://localhost:8082/api/orders/user/user001
```

**Response:**
```json
{
  "orders": [
    {
      "orderId": "ORD-001",
      "userId": "user001",
      "productName": "Laptop",
      "amount": 1299.99,
      "status": "DELIVERED",
      "orderDate": "2026-04-28T..."
    },
    ...
  ],
  "totalOrders": 3
}
```

### Test Recommendation Service
```bash
curl http://localhost:8083/api/recommendations/user/user001
```

### Test Reward Service
```bash
curl http://localhost:8084/api/rewards/user/user001
```

---

## Step 4: Test Aggregator API (The Main Feature!)

This will call **all 4 services in parallel** using CompletableFuture:

```bash
curl http://localhost:8080/api/aggregator/user/user001
```

**Expected Response:**
```json
{
  "userId": "user001",
  "profile": {
    "userId": "user001",
    "name": "John Doe",
    "email": "john.doe@example.com",
    "phone": "+1-555-0101",
    "address": "123 Main St, New York, NY 10001",
    "membershipLevel": "GOLD"
  },
  "orders": {
    "orders": [
      {
        "orderId": "ORD-001",
        "userId": "user001",
        "productName": "Laptop",
        "amount": 1299.99,
        "status": "DELIVERED",
        "orderDate": "2026-04-28T10:30:00"
      },
      ...
    ],
    "totalOrders": 3
  },
  "recommendations": {
    "recommendations": [
      {
        "productId": "PROD-101",
        "productName": "Laptop Stand",
        "category": "Electronics",
        "rating": 4.5,
        "reason": "Based on your recent laptop purchase"
      },
      ...
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
      },
      ...
    ]
  },
  "responseTimeMs": 1247,
  "status": "SUCCESS"
}
```

---

## Performance Comparison

Watch the logs to see the parallel execution!

### Sequential Execution (if done one by one):
```
User Profile:     1000ms
Orders:           1200ms
Recommendations:   800ms
Rewards:           500ms
--------------------------
Total:            3500ms
```

### Parallel Execution (with CompletableFuture):
```
All 4 APIs called simultaneously
--------------------------
Total: ~1200ms (longest service)
Speedup: 2.9x faster! ⚡
```

---

## Prepopulated Test Data

The following user IDs have data in all services:

- **user001** - John Doe (GOLD member, 3 orders, 2500 reward points)
- **user002** - Jane Smith (PLATINUM member, 2 orders, 6000 reward points)
- **user003** - Bob Johnson (SILVER member, 1 order, 750 reward points)
- **test-user-123** - Test User (GOLD member, 2 orders, 2800 reward points)

Test with any of these IDs!

---

## Access H2 Consoles

Each service has its own H2 console:

- User Profile: http://localhost:8081/h2-console
- Orders: http://localhost:8082/h2-console
- Recommendations: http://localhost:8083/h2-console
- Rewards: http://localhost:8084/h2-console

**Connection Settings:**
- JDBC URL: `jdbc:h2:mem:<db-name>db`
- Username: `sa`
- Password: (leave empty)

---

## Key CompletableFuture Code

Located in `AggregatorService.java`:

```java
// Create CompletableFuture for each API call - they run in parallel
CompletableFuture<UserProfile> userProfileFuture = CompletableFuture
    .supplyAsync(() -> userProfileClient.getUserProfile(userId), executorService);

CompletableFuture<OrderResponse> ordersFuture = CompletableFuture
    .supplyAsync(() -> orderClient.getUserOrders(userId), executorService);

CompletableFuture<RecommendationResponse> recommendationsFuture = CompletableFuture
    .supplyAsync(() -> recommendationClient.getRecommendations(userId), executorService);

CompletableFuture<RewardResponse> rewardsFuture = CompletableFuture
    .supplyAsync(() -> rewardClient.getUserRewards(userId), executorService);

// Wait for all futures to complete
CompletableFuture.allOf(
    userProfileFuture,
    ordersFuture,
    recommendationsFuture,
    rewardsFuture
).join();

// Get results from completed futures
UserProfile profile = userProfileFuture.join();
OrderResponse orders = ordersFuture.join();
// ... etc
```

---

## Troubleshooting

### Port Already in Use
If you get "Port already in use" error:
```bash
# Windows - Find process using port
netstat -ano | findstr :8081

# Kill the process
taskkill /PID <process-id> /F
```

### Service Not Responding
1. Check if service started successfully
2. Look at console logs for errors
3. Verify port is correct

### Connection Refused
Make sure all 4 microservices are running before testing the aggregator!

---

## Stopping Services

Press `Ctrl+C` in each terminal to stop the services.

---

## Next Steps

1. ✅ Test all individual services
2. ✅ Test the aggregator with different user IDs
3. ✅ Watch logs to see parallel execution
4. ✅ Compare response times
5. ✅ Explore H2 console to see data
6. ✅ Try the alternative endpoint: `/api/aggregator/user/{userId}/combine`

---

## Summary

You now have a complete working example of:
- ✅ Microservices architecture
- ✅ CompletableFuture for parallel API calls
- ✅ H2 database with prepopulated data
- ✅ RestTemplate (no reactive programming)
- ✅ Fallback mechanisms
- ✅ Performance optimization (3x faster!)

Happy coding! 🚀
