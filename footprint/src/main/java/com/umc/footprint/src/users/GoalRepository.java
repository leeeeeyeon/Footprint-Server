package com.umc.footprint.src.users;

import com.umc.footprint.src.users.model.entity.Goal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GoalRepository extends JpaRepository<Goal,Integer> {

}
