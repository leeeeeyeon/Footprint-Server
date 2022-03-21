package com.umc.footprint.src.repository;

import com.umc.footprint.src.model.Photo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PhotoRepository extends JpaRepository<Photo, Integer> {
}
