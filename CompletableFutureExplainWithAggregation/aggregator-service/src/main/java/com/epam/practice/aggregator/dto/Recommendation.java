package com.epam.practice.aggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Recommendation {
    private String productId;
    private String productName;
    private String category;
    private Double rating;
    private String reason;
}
