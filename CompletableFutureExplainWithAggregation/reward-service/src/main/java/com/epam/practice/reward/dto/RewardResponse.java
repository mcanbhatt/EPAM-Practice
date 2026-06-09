package com.epam.practice.reward.dto;

import com.epam.practice.reward.entity.Reward;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RewardResponse {
    private Integer totalPoints;
    private String tier;
    private List<Reward> rewards;
}
