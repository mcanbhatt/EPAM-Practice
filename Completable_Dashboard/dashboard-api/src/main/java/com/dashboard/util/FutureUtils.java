package com.dashboard.util;

import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Reusable CompletableFuture helpers used across widget services.
 */
@UtilityClass
@Slf4j
public class FutureUtils {

    /**
     * Wraps a supplier in a CompletableFuture with timeout + fallback.
     * Reduces boilerplate in the orchestrator.
     *
     * @param widgetName  used for logging
     * @param supplier    the widget fetch logic
     * @param fallback    value returned on timeout or error
     * @param timeoutSec  per-widget SLA in seconds
     * @param executor    thread pool to run on
     */
    public static <T> CompletableFuture<T> fetchWidget(
            String widgetName,
            Supplier<T> supplier,
            T fallback,
            long timeoutSec,
            Executor executor) {

        return CompletableFuture
                .supplyAsync(supplier, executor)
                .orTimeout(timeoutSec, TimeUnit.SECONDS)
                .exceptionally(ex -> {
                    log.warn("[FutureUtils] Widget '{}' failed or timed out: {}", widgetName, ex.getMessage());
                    return fallback;
                });
    }

    /**
     * Runs two futures in parallel and merges their results.
     * Equivalent to thenCombine pattern.
     */
    public static <A, B, R> CompletableFuture<R> parallel(
            CompletableFuture<A> futureA,
            CompletableFuture<B> futureB,
            java.util.function.BiFunction<A, B, R> merger) {
        return futureA.thenCombine(futureB, merger);
    }

    /**
     * Returns the first future to complete successfully (race pattern).
     * Useful for multi-region fallback scenarios.
     */
    @SuppressWarnings("unchecked")
    public static <T> CompletableFuture<T> race(CompletableFuture<T>... futures) {
        return (CompletableFuture<T>) CompletableFuture.anyOf(futures);
    }
}
