package com.umc.footprint.src.repository;

import com.umc.footprint.src.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TagRepository extends JpaRepository<Tag, Integer> {
}
