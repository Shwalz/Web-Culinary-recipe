package com.example.cookbook.repository;

import com.example.cookbook.domain.Ingredient;
import com.example.cookbook.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    List<Ingredient> findByRecipeId(Long recipeId);
}
