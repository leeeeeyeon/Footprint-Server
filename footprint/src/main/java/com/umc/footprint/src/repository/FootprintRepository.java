package com.umc.footprint.src.repository;

import com.umc.footprint.src.model.Footprint;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FootprintRepository extends JpaRepository<Footprint, Integer> {
}
