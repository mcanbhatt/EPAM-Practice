package com.epam.practice.aggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AggregatedUserData {
    private String userId;
    private UserProfile profile;
    private OrderResponse orders;
    private RecommendationResponse recommendations;
    private RewardResponse rewards;
    private Long responseTimeMs;
    private String status;
}
