package com.epam.practice.aggregator.client;

import com.epam.practice.aggregator.dto.RecommendationResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
//import org.springframework.web.reactive.function.client.WebClient;
//import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.Collections;

@Component
@Slf4j
@RequiredArgsConstructor
public class RecommendationClient {

    //private final WebClient webClient;
	@Autowired
	private final RestTemplate restTemplate;

    @Value("${microservices.recommendations.url}")
    private String recommendationsUrl;

    @Value("${microservices.recommendations.timeout:5000}")
    private int timeout;

    public RecommendationResponse getRecommendations(String userId) {
        log.debug("Fetching recommendations for userId: {}", userId);

        try {
        	
        	return restTemplate.getForObject(recommendationsUrl + "/user/{userId}", RecommendationResponse.class, userId);
           /* return webClient.get()
                    .uri(recommendationsUrl + "/user/{userId}", userId)
                    .retrieve()
                    .bodyToMono(RecommendationResponse.class)
                    .timeout(Duration.ofMillis(timeout))
                    .onErrorResume(error -> {
                        log.error("Error fetching recommendations for userId: {}, error: {}", userId, error.getMessage());
                        return Mono.just(createFallbackRecommendationResponse());
                    })
                    .block();*/
        	
        } catch (Exception e) {
            log.error("Exception fetching recommendations for userId: {}", userId, e);
            return createFallbackRecommendationResponse();
        }
    }

    private RecommendationResponse createFallbackRecommendationResponse() {
        return RecommendationResponse.builder()
                .recommendations(Collections.emptyList())
                .build();
    }
}
