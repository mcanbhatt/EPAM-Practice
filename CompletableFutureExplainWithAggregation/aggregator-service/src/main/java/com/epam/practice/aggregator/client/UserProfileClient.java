package com.epam.practice.aggregator.client;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.epam.practice.aggregator.dto.UserProfile;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserProfileClient {

   // private final WebClient webClient;
	
	@Autowired
	private final RestTemplate restTemplate;

    @Value("${microservices.user-profile.url}")
    private String userProfileUrl;

    @Value("${microservices.user-profile.timeout:5000}")
    private int timeout;

    public UserProfile getUserProfile(String userId) {
        log.debug("Fetching user profile for userId: {}", userId);

        try {
        	
        	//restTemplate.setRequestFactory(new TimeoutRequestFactory(timeout));
        	
        	return restTemplate.getForObject(userProfileUrl + "/{userId}", UserProfile.class, userId);
/*            return webClient.get()
                    .uri(userProfileUrl + "/{userId}", userId)
                    .retrieve()
                    .bodyToMono(UserProfile.class)
                    .timeout(Duration.ofMillis(timeout))
                    .onErrorResume(error -> {
                        log.error("Error fetching user profile for userId: {}, error: {}", userId, error.getMessage());
                        return Mono.just(createFallbackUserProfile(userId));
                    })
                    .block();*/
        } catch (Exception e) {
            log.error("Exception fetching user profile for userId: {}", userId, e);
            return createFallbackUserProfile(userId);
        }
    }

    private UserProfile createFallbackUserProfile(String userId) {
        return UserProfile.builder()
                .userId(userId)
                .name("User-" + userId)
                .email(userId + "@example.com")
                .membershipLevel("BASIC")
                .build();
    }
}
