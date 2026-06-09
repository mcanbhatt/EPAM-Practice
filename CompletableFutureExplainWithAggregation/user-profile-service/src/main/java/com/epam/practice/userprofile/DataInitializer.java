package com.epam.practice.userprofile;

import com.epam.practice.userprofile.entity.UserProfile;
import com.epam.practice.userprofile.repository.UserProfileRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final UserProfileRepository repository;

    @Override
    public void run(String... args) {
        log.info("Initializing user profile data...");

        repository.saveAll(Arrays.asList(
            UserProfile.builder()
                .userId("user001")
                .name("John Doe")
                .email("john.doe@example.com")
                .phone("+1-555-0101")
                .address("123 Main St, New York, NY 10001")
                .membershipLevel("GOLD")
                .build(),
            UserProfile.builder()
                .userId("user002")
                .name("Jane Smith")
                .email("jane.smith@example.com")
                .phone("+1-555-0102")
                .address("456 Oak Ave, Los Angeles, CA 90001")
                .membershipLevel("PLATINUM")
                .build(),
            UserProfile.builder()
                .userId("user003")
                .name("Bob Johnson")
                .email("bob.johnson@example.com")
                .phone("+1-555-0103")
                .address("789 Pine Rd, Chicago, IL 60601")
                .membershipLevel("SILVER")
                .build(),
            UserProfile.builder()
                .userId("test-user-123")
                .name("Test User")
                .email("test.user@example.com")
                .phone("+1-555-9999")
                .address("999 Test St, TestCity, TC 99999")
                .membershipLevel("GOLD")
                .build()
        ));

        log.info("User profile data initialized successfully. Total records: {}", repository.count());
    }
}
