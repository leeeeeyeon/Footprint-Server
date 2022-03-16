package com.umc.footprint.src.repository;

import com.umc.footprint.src.model.UserBadge;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserBadgeRepository extends JpaRepository<UserBadge, Integer> {
}
