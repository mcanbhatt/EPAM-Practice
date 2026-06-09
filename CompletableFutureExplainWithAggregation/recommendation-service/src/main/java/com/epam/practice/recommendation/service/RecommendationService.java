package com.epam.practice.recommendation.service;

import com.epam.practice.recommendation.dto.RecommendationResponse;
import com.epam.practice.recommendation.entity.Recommendation;
import com.epam.practice.recommendation.repository.RecommendationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class RecommendationService {

    private final RecommendationRepository repository;

    public RecommendationResponse getRecommendations(String userId) {
        log.info("Fetching recommendations for userId: {}", userId);

        // Simulate some processing delay
        simulateDelay(800);

        List<Recommendation> recommendations = repository.findByUserId(userId);

        return RecommendationResponse.builder()
                .recommendations(recommendations)
                .build();
    }

    private void simulateDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
