package com.dashboard.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class CacheConfig {

    /** Per-widget TTL overrides (seconds) */
    public static final Map<String, Duration> WIDGET_TTL = Map.of(
            "widget:charts",   Duration.ofSeconds(60),
            "widget:profile",  Duration.ofSeconds(300),
            "widget:alerts",   Duration.ofSeconds(30),
            "widget:revenue",  Duration.ofSeconds(120),
            "widget:stock",    Duration.ofSeconds(60),
            "widget:tasks",    Duration.ofSeconds(45)
    );

    @Bean
  //  @Primary
    public RedisCacheConfiguration defaultCacheConfig() {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(60))
                .serializeValuesWith(
                        RedisSerializationContext.SerializationPair
                                .fromSerializer(new GenericJackson2JsonRedisSerializer()));
    }

    @Bean
    public CacheManager redisCacheManager(RedisConnectionFactory factory,
                                          RedisCacheConfiguration defaultCacheConfig) {
        Map<String, RedisCacheConfiguration> perCache = new HashMap<>();
        WIDGET_TTL.forEach((name, ttl) ->
                perCache.put(name, defaultCacheConfig.entryTtl(ttl)));

        return RedisCacheManager.builder(factory)
                .cacheDefaults(defaultCacheConfig)
                .withInitialCacheConfigurations(perCache)
                .build();
    }

    /** Fallback in-memory cache when Redis is not available */
    @Bean("inMemoryCacheManager")
    @Primary
    public CacheManager inMemoryCacheManager() {
        return new ConcurrentMapCacheManager(
                "widget:charts", "widget:profile", "widget:alerts",
                "widget:revenue", "widget:stock", "widget:tasks");
    }
}
