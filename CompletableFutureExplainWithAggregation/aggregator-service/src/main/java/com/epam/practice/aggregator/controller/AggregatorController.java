package com.epam.practice.aggregator.controller;

import com.epam.practice.aggregator.dto.AggregatedUserData;
import com.epam.practice.aggregator.service.AggregatorService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/aggregator")
@RequiredArgsConstructor
@Slf4j
public class AggregatorController {

    private final AggregatorService aggregatorService;

    /**
     * Main endpoint to get aggregated user data from all microservices
     * GET /api/aggregator/user/{userId}
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<AggregatedUserData> getAggregatedUserData(@PathVariable String userId) {
        log.info("Received request for aggregated data for userId: {}", userId);

        try {
            AggregatedUserData aggregatedData = aggregatorService.aggregateUserData(userId);
            return ResponseEntity.ok(aggregatedData);
        } catch (Exception e) {
            log.error("Error processing request for userId: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AggregatedUserData.builder()
                            .userId(userId)
                            .status("ERROR")
                            .build());
        }
    }

    /**
     * Alternative endpoint using thenCombine approach
     * GET /api/aggregator/user/{userId}/combine
     */
    @GetMapping("/user/{userId}/combine")
    public ResponseEntity<AggregatedUserData> getAggregatedUserDataWithCombine(@PathVariable String userId) {
        log.info("Received request (combine) for aggregated data for userId: {}", userId);

        try {
            AggregatedUserData aggregatedData = aggregatorService.aggregateUserDataWithCombine(userId);
            return ResponseEntity.ok(aggregatedData);
        } catch (Exception e) {
            log.error("Error processing request (combine) for userId: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AggregatedUserData.builder()
                            .userId(userId)
                            .status("ERROR")
                            .build());
        }
    }
    
    /**
     * Alternative endpoint using sequaential approach
     * GET /api/aggregator//users/{userId}/sequential
     */
    @GetMapping("/users/{userId}/sequential")
    public ResponseEntity<AggregatedUserData> getAggregatedUserDataWithSeq(@PathVariable String userId) {
        log.info("Received request (combine) for aggregated data for userId: {}", userId);

        try {
            AggregatedUserData aggregatedData = aggregatorService.aggregateSequantially(userId);
            return ResponseEntity.ok(aggregatedData);
        } catch (Exception e) {
            log.error("Error processing request (combine) for userId: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AggregatedUserData.builder()
                            .userId(userId)
                            .status("ERROR")
                            .build());
        }
    }
    

    /**
     * Endpoint demonstrating thenApply - synchronous transformation
     * GET /api/aggregator/user/{userId}/then-apply
     */
    @GetMapping("/user/{userId}/then-apply")
    public ResponseEntity<AggregatedUserData> getUserWithThenApply(@PathVariable String userId) {
        log.info("Received request (thenApply) for userId: {}", userId);

        try {
            AggregatedUserData result = aggregatorService.getUserWithThenApply(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing request (thenApply) for userId: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AggregatedUserData.builder()
                            .userId(userId)
                            .status("ERROR")
                            .build());
        }
    }

    /**
     * Endpoint demonstrating thenApplyAsync - asynchronous transformation
     * GET /api/aggregator/user/{userId}/then-apply-async
     */
    @GetMapping("/user/{userId}/then-apply-async")
    public ResponseEntity<AggregatedUserData> getUserWithThenApplyAsync(@PathVariable String userId) {
        log.info("Received request (thenApplyAsync) for userId: {}", userId);

        try {
            AggregatedUserData result = aggregatorService.getUserWithThenApplyAsync(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing request (thenApplyAsync) for userId: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AggregatedUserData.builder()
                            .userId(userId)
                            .status("ERROR")
                            .build());
        }
    }

    /**
     * Endpoint demonstrating thenAcceptBoth - processing two futures together
     * GET /api/aggregator/user/{userId}/then-accept-both
     */
    @GetMapping("/user/{userId}/then-accept-both")
    public ResponseEntity<AggregatedUserData> getUserWithThenAcceptBoth(@PathVariable String userId) {
        log.info("Received request (thenAcceptBoth) for userId: {}", userId);

        try {
            AggregatedUserData result = aggregatorService.getUserWithThenAcceptBoth(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing request (thenAcceptBoth) for userId: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AggregatedUserData.builder()
                            .userId(userId)
                            .status("ERROR")
                            .build());
        }
    }

    /**
     * Endpoint demonstrating applyToEither - using the fastest result
     * GET /api/aggregator/user/{userId}/apply-to-either
     */
    @GetMapping("/user/{userId}/apply-to-either")
    public ResponseEntity<AggregatedUserData> getUserWithApplyToEither(@PathVariable String userId) {
        log.info("Received request (applyToEither) for userId: {}", userId);

        try {
            AggregatedUserData result = aggregatorService.getUserWithApplyToEither(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing request (applyToEither) for userId: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AggregatedUserData.builder()
                            .userId(userId)
                            .status("ERROR")
                            .build());
        }
    }

    /**
     * Endpoint demonstrating anyOf - first completed future
     * GET /api/aggregator/user/{userId}/any-of
     */
    @GetMapping("/user/{userId}/any-of")
    public ResponseEntity<AggregatedUserData> getUserWithAnyOf(@PathVariable String userId) {
        log.info("Received request (anyOf) for userId: {}", userId);

        try {
            AggregatedUserData result = aggregatorService.getUserWithAnyOf(userId);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            log.error("Error processing request (anyOf) for userId: {}", userId, e);
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(AggregatedUserData.builder()
                            .userId(userId)
                            .status("ERROR")
                            .build());
        }
    }

    /**
     * Health check endpoint
     */
    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Aggregator API is running!");
    }
}
