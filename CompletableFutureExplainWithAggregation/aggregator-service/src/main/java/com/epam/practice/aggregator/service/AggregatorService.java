package com.epam.practice.aggregator.service;

import com.epam.practice.aggregator.client.OrderClient;
import com.epam.practice.aggregator.client.RecommendationClient;
import com.epam.practice.aggregator.client.RewardClient;
import com.epam.practice.aggregator.client.UserProfileClient;
import com.epam.practice.aggregator.dto.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AggregatorService {

    private final UserProfileClient userProfileClient;
    private final OrderClient orderClient;
    private final RecommendationClient recommendationClient;
    private final RewardClient rewardClient;

    // Custom thread pool for parallel API calls
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    /**
     * Aggregates data from multiple microservices in parallel using CompletableFuture
     * This significantly reduces response time compared to sequential calls
     */
    public AggregatedUserData aggregateUserData(String userId) {
    	 long startTime = System.currentTimeMillis();
        log.info("Starting data aggregation for userId: {} in {}ms ", userId, startTime);

        try {
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
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    userProfileFuture,
                    ordersFuture,
                    recommendationsFuture,
                    rewardsFuture
            );

            // Block until all complete
            allFutures.join();

            // Get results from completed futures
            UserProfile profile = userProfileFuture.join();
            OrderResponse orders = ordersFuture.join();
            RecommendationResponse recommendations = recommendationsFuture.join();
            RewardResponse rewards = rewardsFuture.join();

            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            log.info("Data aggregation completed for userId: {} in {}ms", userId, responseTime);

            return AggregatedUserData.builder()
                    .userId(userId)
                    .profile(profile)
                    .orders(orders)
                    .recommendations(recommendations)
                    .rewards(rewards)
                    .responseTimeMs(responseTime)
                    .status("SUCCESS")
                    .build();

        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            long responseTime = endTime - startTime;

            log.error("Error aggregating data for userId: {}", userId, e);

            return AggregatedUserData.builder()
                    .userId(userId)
                    .responseTimeMs(responseTime)
                    .status("PARTIAL_FAILURE")
                    .build();
        }
    }

    /**
     * Alternative implementation using thenCombine for dependent operations
     */
    public AggregatedUserData aggregateUserDataWithCombine(String userId) {
        log.info("Starting data aggregation (with combine) for userId: {}", userId);
        long startTime = System.currentTimeMillis();

        CompletableFuture<UserProfile> userProfileFuture = CompletableFuture
                .supplyAsync(() -> userProfileClient.getUserProfile(userId), executorService);

        CompletableFuture<OrderResponse> ordersFuture = CompletableFuture
                .supplyAsync(() -> orderClient.getUserOrders(userId), executorService);

        CompletableFuture<RecommendationResponse> recommendationsFuture = CompletableFuture
                .supplyAsync(() -> recommendationClient.getRecommendations(userId), executorService);

        CompletableFuture<RewardResponse> rewardsFuture = CompletableFuture
                .supplyAsync(() -> rewardClient.getUserRewards(userId), executorService);

        // Combine all futures
        CompletableFuture<AggregatedUserData> aggregatedFuture = userProfileFuture
                .thenCombine(ordersFuture, (profile, orders) ->
                        AggregatedUserData.builder()
                                .userId(userId)
                                .profile(profile)
                                .orders(orders)
                                .build())
                .thenCombine(recommendationsFuture, (aggregated, recommendations) -> {
                    aggregated.setRecommendations(recommendations);
                    return aggregated;
                })
                .thenCombine(rewardsFuture, (aggregated, rewards) -> {
                    aggregated.setRewards(rewards);
                    long responseTime = System.currentTimeMillis() - startTime;
                    aggregated.setResponseTimeMs(responseTime);
                    aggregated.setStatus("SUCCESS");
                    return aggregated;
                });

        AggregatedUserData result = aggregatedFuture.join();
        log.info("Data aggregation (with combine) completed for userId: {} in {}ms",
                userId, result.getResponseTimeMs());

        return result;
    }
    
    
    
    /**
     * Alternative implementation using thenCombine for dependent operations
     * sequentially combines results as they become available, which can be more efficient if some calls depend on others
     */
    public AggregatedUserData aggregateSequantially(String userId) {
        log.info("Starting data aggregation (with Sequantially) for userId: {}", userId);
        long startTime = System.currentTimeMillis();

        UserProfile userProfile = userProfileClient.getUserProfile(userId);
        OrderResponse orders = orderClient.getUserOrders(userId);

        RecommendationResponse recommendations = recommendationClient.getRecommendations(userId);

        RewardResponse rewards = rewardClient.getUserRewards(userId);

        AggregatedUserData result  =  AggregatedUserData.builder()
        .userId(userId)
        .profile(userProfile)
        .orders(orders).recommendations(recommendations).
        rewards(rewards).status("SUCCESS")
        .build();
        long responseTime = System.currentTimeMillis() - startTime;
        
       log.info("Data aggregation (with combine) completed for userId: {} in {} ms",
                userId, responseTime);

        return result;
    }    
    
    

    /**
     * Demonstrates thenApply - transforming the result of a CompletableFuture
     * Fetches user profile and transforms it to include a custom message
     */
    public AggregatedUserData getUserWithThenApply(String userId) {
        log.info("Fetching user with thenApply for userId: {}", userId);
        long startTime = System.currentTimeMillis();

        CompletableFuture<AggregatedUserData> result = CompletableFuture
                .supplyAsync(() -> userProfileClient.getUserProfile(userId), executorService)
                .thenApply(profile -> {
                    log.info("Transforming profile data for userId: {}", userId);
                    long responseTime = System.currentTimeMillis() - startTime;
                    return AggregatedUserData.builder()
                            .userId(userId)
                            .profile(profile)
                            .status("SUCCESS - Transformed with thenApply")
                            .responseTimeMs(responseTime)
                            .build();
                });

        return result.join();
    }

    /**
     * Demonstrates thenApplyAsync - transforming the result asynchronously in a different thread
     */
    public AggregatedUserData getUserWithThenApplyAsync(String userId) {
        log.info("Fetching user with thenApplyAsync for userId: {}", userId);
        long startTime = System.currentTimeMillis();

        CompletableFuture<AggregatedUserData> result = CompletableFuture
                .supplyAsync(() -> userProfileClient.getUserProfile(userId), executorService)
                .thenApplyAsync(profile -> {
                    log.info("Async transformation on thread: {}", Thread.currentThread().getName());
                    long responseTime = System.currentTimeMillis() - startTime;
                    return AggregatedUserData.builder()
                            .userId(userId)
                            .profile(profile)
                            .status("SUCCESS - Transformed with thenApplyAsync")
                            .responseTimeMs(responseTime)
                            .build();
                }, executorService);

        return result.join();
    }

    /**
     * Demonstrates thenAcceptBoth - performs an action when both futures complete
     * Fetches orders and rewards and logs them together
     */
    public AggregatedUserData getUserWithThenAcceptBoth(String userId) {
        log.info("Fetching user with thenAcceptBoth for userId: {}", userId);
        long startTime = System.currentTimeMillis();

        CompletableFuture<OrderResponse> ordersFuture = CompletableFuture
                .supplyAsync(() -> orderClient.getUserOrders(userId), executorService);

        CompletableFuture<RewardResponse> rewardsFuture = CompletableFuture
                .supplyAsync(() -> rewardClient.getUserRewards(userId), executorService);

        // Create a holder for the result
        AggregatedUserData.AggregatedUserDataBuilder builder = AggregatedUserData.builder()
                .userId(userId);

        // Use thenAcceptBoth to process both results when they complete
        ordersFuture.thenAcceptBoth(rewardsFuture, (orders, rewards) -> {
            log.info("Both orders and rewards received for userId: {}. Orders: {}, Rewards: {}",
                    userId, orders.getOrders().size(), rewards.getRewards().size());
            builder.orders(orders).rewards(rewards);
        }).join();

        long responseTime = System.currentTimeMillis() - startTime;
        return builder
                .status("SUCCESS - Processed with thenAcceptBoth")
                .responseTimeMs(responseTime)
                .build();
    }

    /**
     * Demonstrates applyToEither - uses whichever future completes first
     * Fetches recommendations from two different sources and uses the faster one
     */
    public AggregatedUserData getUserWithApplyToEither(String userId) {
        log.info("Fetching user with applyToEither for userId: {}", userId);
        long startTime = System.currentTimeMillis();

        // Simulate two sources that might have different response times
        CompletableFuture<RecommendationResponse> recommendationsFuture1 = CompletableFuture
                .supplyAsync(() -> {
                    log.info("Source 1 fetching recommendations");
                    return recommendationClient.getRecommendations(userId);
                }, executorService);

        CompletableFuture<RecommendationResponse> recommendationsFuture2 = CompletableFuture
                .supplyAsync(() -> {
                    log.info("Source 2 fetching recommendations");
                    try {
                        Thread.sleep(100); // Simulate slight delay
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                    return recommendationClient.getRecommendations(userId);
                }, executorService);

        // Use applyToEither to get the first completed result
        CompletableFuture<AggregatedUserData> result = recommendationsFuture1
                .applyToEither(recommendationsFuture2, recommendations -> {
                    log.info("Using faster recommendation source for userId: {}", userId);
                    long responseTime = System.currentTimeMillis() - startTime;
                    return AggregatedUserData.builder()
                            .userId(userId)
                            .recommendations(recommendations)
                            .status("SUCCESS - Fastest result with applyToEither")
                            .responseTimeMs(responseTime)
                            .build();
                });

        return result.join();
    }

    /**
     * Demonstrates anyOf - returns the first completed future among multiple
     * Fetches data from all services and returns as soon as any one completes
     */
    public AggregatedUserData getUserWithAnyOf(String userId) {
        log.info("Fetching user with anyOf for userId: {}", userId);
        long startTime = System.currentTimeMillis();

        CompletableFuture<String> userProfileFuture = CompletableFuture
                .supplyAsync(() -> {
                    userProfileClient.getUserProfile(userId);
                    return "UserProfile completed first";
                }, executorService);

        CompletableFuture<String> ordersFuture = CompletableFuture
                .supplyAsync(() -> {
                    orderClient.getUserOrders(userId);
                    return "Orders completed first";
                }, executorService);

        CompletableFuture<String> recommendationsFuture = CompletableFuture
                .supplyAsync(() -> {
                    recommendationClient.getRecommendations(userId);
                    return "Recommendations completed first";
                }, executorService);

        CompletableFuture<String> rewardsFuture = CompletableFuture
                .supplyAsync(() -> {
                    rewardClient.getUserRewards(userId);
                    return "Rewards completed first";
                }, executorService);

        // anyOf returns the first completed future
        CompletableFuture<Object> firstCompleted = CompletableFuture.anyOf(
                userProfileFuture, ordersFuture, recommendationsFuture, rewardsFuture);

        String firstService = (String) firstCompleted.join();
        long responseTime = System.currentTimeMillis() - startTime;

        log.info("First completed service: {} for userId: {}", firstService, userId);

        return AggregatedUserData.builder()
                .userId(userId)
                .status("SUCCESS - First completed: " + firstService + " (using anyOf)")
                .responseTimeMs(responseTime)
                .build();
    }

    /**
     * Graceful shutdown of executor service
     */
    public void shutdown() {
        log.info("Shutting down executor service");
        executorService.shutdown();
    }
}
