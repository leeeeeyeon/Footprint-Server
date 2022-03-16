package com.umc.footprint.src.repository;

import com.umc.footprint.src.model.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal, Integer> {
}
