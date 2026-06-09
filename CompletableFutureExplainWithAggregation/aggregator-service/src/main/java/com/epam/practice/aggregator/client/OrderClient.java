package com.epam.practice.aggregator.client;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.epam.practice.aggregator.dto.OrderResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
@RequiredArgsConstructor
public class OrderClient {

   // private final WebClient webClient;
	
	@Autowired
	private final RestTemplate restTemplate;

    @Value("${microservices.orders.url}")
    private String ordersUrl;

    @Value("${microservices.orders.timeout:5000}")
    private int timeout;

    public OrderResponse getUserOrders(String userId) {
        log.debug("Fetching orders for userId: {}", userId);

        try {
          /*  return webClient.get()
                    .uri(ordersUrl + "/user/{userId}", userId)
                    .retrieve()
                    .bodyToMono(OrderResponse.class)
                    .timeout(Duration.ofMillis(timeout))
                    .onErrorResume(error -> {
                        log.error("Error fetching orders for userId: {}, error: {}", userId, error.getMessage());
                        return Mono.just(createFallbackOrderResponse());
                    })
                    .block();*/
        	
        	return restTemplate.getForObject(ordersUrl + "/user/{userId}", OrderResponse.class, userId);
        } catch (Exception e) {
            log.error("Exception fetching orders for userId: {}", userId, e);
            return createFallbackOrderResponse();
        }
    }

    private OrderResponse createFallbackOrderResponse() {
        return OrderResponse.builder()
                .orders(Collections.emptyList())
                .totalOrders(0)
                .build();
    }
}
