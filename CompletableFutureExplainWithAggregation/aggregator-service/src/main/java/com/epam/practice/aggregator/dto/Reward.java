package com.epam.practice.aggregator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Reward {
    private String rewardId;
    private String type;
    private Integer points;
    private String description;
    private Boolean isActive;
}
