package com.epam.practice.reward.repository;

import com.epam.practice.reward.entity.Reward;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RewardRepository extends JpaRepository<Reward, Long> {
    List<Reward> findByUserId(String userId);

    @Query("SELECT SUM(r.points) FROM Reward r WHERE r.userId = :userId AND r.isActive = true")
    Integer getTotalPointsByUserId(@Param("userId") String userId);
}
