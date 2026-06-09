package com.epam.practice.reward.service;

import com.epam.practice.reward.dto.RewardResponse;
import com.epam.practice.reward.entity.Reward;
import com.epam.practice.reward.repository.RewardRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RewardService {

    private final RewardRepository repository;

    public RewardResponse getUserRewards(String userId) {
        log.info("Fetching rewards for userId: {}", userId);

        // Simulate some processing delay
        simulateDelay(500);

        List<Reward> rewards = repository.findByUserId(userId);
        Integer totalPoints = repository.getTotalPointsByUserId(userId);

        if (totalPoints == null) {
            totalPoints = 0;
        }

        String tier = determineTier(totalPoints);

        return RewardResponse.builder()
                .totalPoints(totalPoints)
                .tier(tier)
                .rewards(rewards)
                .build();
    }

    private String determineTier(Integer totalPoints) {
        if (totalPoints >= 5000) {
            return "PLATINUM";
        } else if (totalPoints >= 2000) {
            return "GOLD";
        } else if (totalPoints >= 500) {
            return "SILVER";
        } else {
            return "BRONZE";
        }
    }

    private void simulateDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
