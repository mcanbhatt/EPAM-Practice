package com.epam.practice.recommendation.controller;

import com.epam.practice.recommendation.dto.RecommendationResponse;
import com.epam.practice.recommendation.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/recommendations")
@RequiredArgsConstructor
@Slf4j
public class RecommendationController {

    private final RecommendationService service;

    @GetMapping("/user/{userId}")
    public ResponseEntity<RecommendationResponse> getRecommendations(@PathVariable String userId) {
        log.info("Received request for recommendations of userId: {}", userId);
        RecommendationResponse response = service.getRecommendations(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Recommendation Service is running!");
    }
}
