package com.epam.practice.recommendation;

import com.epam.practice.recommendation.entity.Recommendation;
import com.epam.practice.recommendation.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RecommendationRepository repository;

    @Override
    public void run(String... args) {
        log.info("Initializing recommendation data...");

        repository.saveAll(Arrays.asList(
            Recommendation.builder()
                .userId("user001")
                .productId("PROD-101")
                .productName("Laptop Stand")
                .category("Electronics")
                .rating(4.5)
                .reason("Based on your recent laptop purchase")
                .build(),
            Recommendation.builder()
                .userId("user001")
                .productId("PROD-102")
                .productName("USB Hub")
                .category("Electronics")
                .rating(4.3)
                .reason("Frequently bought together")
                .build(),
            Recommendation.builder()
                .userId("user001")
                .productId("PROD-103")
                .productName("Wireless Keyboard")
                .category("Electronics")
                .rating(4.7)
                .reason("Customers also viewed")
                .build(),
            Recommendation.builder()
                .userId("user002")
                .productId("PROD-201")
                .productName("Phone Case")
                .category("Accessories")
                .rating(4.6)
                .reason("Perfect fit for your smartphone")
                .build(),
            Recommendation.builder()
                .userId("user002")
                .productId("PROD-202")
                .productName("Screen Protector")
                .category("Accessories")
                .rating(4.4)
                .reason("Protect your investment")
                .build(),
            Recommendation.builder()
                .userId("user003")
                .productId("PROD-301")
                .productName("HDMI Cable")
                .category("Electronics")
                .rating(4.2)
                .reason("For your new monitor")
                .build(),
            Recommendation.builder()
                .userId("test-user-123")
                .productId("PROD-401")
                .productName("Mouse Pad")
                .category("Accessories")
                .rating(4.5)
                .reason("Complement your gaming mouse")
                .build(),
            Recommendation.builder()
                .userId("test-user-123")
                .productId("PROD-402")
                .productName("Keycap Set")
                .category("Accessories")
                .rating(4.8)
                .reason("Customize your mechanical keyboard")
                .build(),
            Recommendation.builder()
                .userId("test-user-123")
                .productId("PROD-403")
                .productName("Gaming Headset")
                .category("Electronics")
                .rating(4.6)
                .reason("Complete your gaming setup")
                .build()
        ));

        log.info("Recommendation data initialized successfully. Total records: {}", repository.count());
    }
}
