package com.epam.practice.aggregator.client;

import com.epam.practice.aggregator.dto.RewardResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
// org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class RewardClient {

    //private final WebClient webClient;
    
    @Autowired
	private final RestTemplate restTemplate;

    @Value("${microservices.rewards.url}")
    private String rewardsUrl;

    @Value("${microservices.rewards.timeout:5000}")
    private int timeout;

    public RewardResponse getUserRewards(String userId) {
        log.debug("Fetching rewards for userId: {}", userId);

        try {
           
        	return restTemplate.getForObject(rewardsUrl + "/user/{userId}", RewardResponse.class, userId);
        	/*return webClient.get()
                    .uri(rewardsUrl + "/user/{userId}", userId)
                    .retrieve()
                    .bodyToMono(RewardResponse.class)
                    .timeout(Duration.ofMillis(timeout))
                    .onErrorResume(error -> {
                        log.error("Error fetching rewards for userId: {}, error: {}", userId, error.getMessage());
                        return Mono.just(createFallbackRewardResponse());
                    })
                    .block();*/
        } catch (Exception e) {
            log.error("Exception fetching rewards for userId: {}", userId, e);
            return createFallbackRewardResponse();
        }
    }

    private RewardResponse createFallbackRewardResponse() {
        return RewardResponse.builder()
                .totalPoints(0)
                .tier("NONE")
                .rewards(Collections.emptyList())
                .build();
    }
}
