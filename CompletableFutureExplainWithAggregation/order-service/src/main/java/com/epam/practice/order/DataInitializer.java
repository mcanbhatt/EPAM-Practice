package com.epam.practice.order;

import com.epam.practice.order.entity.Order;
import com.epam.practice.order.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final OrderRepository repository;

    @Override
    public void run(String... args) {
        log.info("Initializing order data...");

        repository.saveAll(Arrays.asList(
            Order.builder()
                .orderId("ORD-001")
                .userId("user001")
                .productName("Laptop")
                .amount(new BigDecimal("1299.99"))
                .status("DELIVERED")
                .orderDate(LocalDateTime.now().minusDays(10))
                .build(),
            Order.builder()
                .orderId("ORD-002")
                .userId("user001")
                .productName("Wireless Mouse")
                .amount(new BigDecimal("29.99"))
                .status("DELIVERED")
                .orderDate(LocalDateTime.now().minusDays(8))
                .build(),
            Order.builder()
                .orderId("ORD-003")
                .userId("user001")
                .productName("USB-C Cable")
                .amount(new BigDecimal("12.99"))
                .status("SHIPPED")
                .orderDate(LocalDateTime.now().minusDays(2))
                .build(),
            Order.builder()
                .orderId("ORD-004")
                .userId("user002")
                .productName("Smartphone")
                .amount(new BigDecimal("899.99"))
                .status("DELIVERED")
                .orderDate(LocalDateTime.now().minusDays(15))
                .build(),
            Order.builder()
                .orderId("ORD-005")
                .userId("user002")
                .productName("Headphones")
                .amount(new BigDecimal("199.99"))
                .status("PROCESSING")
                .orderDate(LocalDateTime.now().minusDays(1))
                .build(),
            Order.builder()
                .orderId("ORD-006")
                .userId("user003")
                .productName("Monitor")
                .amount(new BigDecimal("349.99"))
                .status("DELIVERED")
                .orderDate(LocalDateTime.now().minusDays(20))
                .build(),
            Order.builder()
                .orderId("ORD-007")
                .userId("test-user-123")
                .productName("Mechanical Keyboard")
                .amount(new BigDecimal("149.99"))
                .status("DELIVERED")
                .orderDate(LocalDateTime.now().minusDays(5))
                .build(),
            Order.builder()
                .orderId("ORD-008")
                .userId("test-user-123")
                .productName("Gaming Mouse")
                .amount(new BigDecimal("79.99"))
                .status("SHIPPED")
                .orderDate(LocalDateTime.now().minusDays(3))
                .build()
        ));

        log.info("Order data initialized successfully. Total records: {}", repository.count());
    }
}
