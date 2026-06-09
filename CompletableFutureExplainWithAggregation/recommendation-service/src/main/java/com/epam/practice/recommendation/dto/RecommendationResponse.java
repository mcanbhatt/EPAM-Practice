package com.epam.practice.recommendation.dto;

import com.epam.practice.recommendation.entity.Recommendation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendationResponse {
    private List<Recommendation> recommendations;
}
