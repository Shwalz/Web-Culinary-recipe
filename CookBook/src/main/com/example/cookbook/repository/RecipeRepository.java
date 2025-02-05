package com.example.cookbook.repository;

import com.example.cookbook.domain.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findByNameContainingIgnoreCase(String keyword);
    List<Recipe> findByCategoryId(Long categoryId);
}