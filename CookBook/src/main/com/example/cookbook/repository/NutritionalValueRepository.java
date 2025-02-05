package com.example.cookbook.repository;

import com.example.cookbook.domain.NutritionalValue;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NutritionalValueRepository extends JpaRepository<NutritionalValue, Long> {
}
