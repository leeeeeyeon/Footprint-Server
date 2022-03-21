package com.umc.footprint.src.repository;

import com.umc.footprint.src.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
}
