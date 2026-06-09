package com.epam.practice.order.service;

import com.epam.practice.order.dto.OrderResponse;
import com.epam.practice.order.entity.Order;
import com.epam.practice.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository repository;

    public OrderResponse getUserOrders(String userId) {
        log.info("Fetching orders for userId: {}", userId);

        // Simulate some processing delay
        simulateDelay(1200);

        List<Order> orders = repository.findByUserId(userId);

        return OrderResponse.builder()
                .orders(orders)
                .totalOrders(orders.size())
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
