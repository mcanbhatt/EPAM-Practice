package com.epam.practice.userprofile.service;

import com.epam.practice.userprofile.entity.UserProfile;
import com.epam.practice.userprofile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserProfileService {

    private final UserProfileRepository repository;

    public UserProfile getUserProfile(String userId) {
        log.info("Fetching user profile for userId: {}", userId);

        // Simulate some processing delay
        simulateDelay(1000);

        return repository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
    }

    private void simulateDelay(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
