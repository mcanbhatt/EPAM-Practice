package com.epam.practice.reward;

import com.epam.practice.reward.entity.Reward;
import com.epam.practice.reward.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final RewardRepository repository;

    @Override
    public void run(String... args) {
        log.info("Initializing reward data...");

        repository.saveAll(Arrays.asList(
            Reward.builder()
                .userId("user001")
                .rewardId("REW-001")
                .type("CASHBACK")
                .points(500)
                .description("5% cashback on next purchase")
                .isActive(true)
                .build(),
            Reward.builder()
                .userId("user001")
                .rewardId("REW-002")
                .type("POINTS")
                .points(1500)
                .description("Loyalty points")
                .isActive(true)
                .build(),
            Reward.builder()
                .userId("user001")
                .rewardId("REW-003")
                .type("DISCOUNT")
                .points(500)
                .description("$50 off on orders above $500")
                .isActive(true)
                .build(),
            Reward.builder()
                .userId("user002")
                .rewardId("REW-004")
                .type("CASHBACK")
                .points(1000)
                .description("10% cashback")
                .isActive(true)
                .build(),
            Reward.builder()
                .userId("user002")
                .rewardId("REW-005")
                .type("POINTS")
                .points(5000)
                .description("Platinum member points")
                .isActive(true)
                .build(),
            Reward.builder()
                .userId("user003")
                .rewardId("REW-006")
                .type("POINTS")
                .points(750)
                .description("Welcome bonus")
                .isActive(true)
                .build(),
            Reward.builder()
                .userId("test-user-123")
                .rewardId("REW-007")
                .type("CASHBACK")
                .points(800)
                .description("Gaming gear cashback")
                .isActive(true)
                .build(),
            Reward.builder()
                .userId("test-user-123")
                .rewardId("REW-008")
                .type("POINTS")
                .points(1700)
                .description("Loyalty rewards")
                .isActive(true)
                .build(),
            Reward.builder()
                .userId("test-user-123")
                .rewardId("REW-009")
                .type("DISCOUNT")
                .points(300)
                .description("Special member discount")
                .isActive(true)
                .build()
        ));

        log.info("Reward data initialized successfully. Total records: {}", repository.count());
    }
}
