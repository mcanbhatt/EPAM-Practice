package com.epam.practice.reward.controller;

import com.epam.practice.reward.dto.RewardResponse;
import com.epam.practice.reward.service.RewardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/rewards")
@RequiredArgsConstructor
@Slf4j
public class RewardController {

    private final RewardService service;

    @GetMapping("/user/{userId}")
    public ResponseEntity<RewardResponse> getUserRewards(@PathVariable String userId) {
        log.info("Received request for rewards of userId: {}", userId);
        RewardResponse response = service.getUserRewards(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Reward Service is running!");
    }
}
