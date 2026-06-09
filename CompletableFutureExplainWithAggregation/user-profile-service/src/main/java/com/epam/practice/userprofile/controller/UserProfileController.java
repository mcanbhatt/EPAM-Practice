package com.epam.practice.userprofile.controller;

import com.epam.practice.userprofile.entity.UserProfile;
import com.epam.practice.userprofile.service.UserProfileService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@Slf4j
public class UserProfileController {

    private final UserProfileService service;

    @GetMapping("/{userId}")
    public ResponseEntity<UserProfile> getUserProfile(@PathVariable String userId) {
        log.info("Received request for userId: {}", userId);
        UserProfile profile = service.getUserProfile(userId);
        return ResponseEntity.ok(profile);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("User Profile Service is running!");
    }
}
