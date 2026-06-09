package com.epam.practice.order.controller;

import com.epam.practice.order.dto.OrderResponse;
import com.epam.practice.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@Slf4j
public class OrderController {

    private final OrderService service;

    @GetMapping("/user/{userId}")
    public ResponseEntity<OrderResponse> getUserOrders(@PathVariable String userId) {
        log.info("Received request for orders of userId: {}", userId);
        OrderResponse response = service.getUserOrders(userId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Order Service is running!");
    }
}
