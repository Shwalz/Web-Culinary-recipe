package com.example.cookbook.domain;

import jakarta.persistence.Embeddable;

@Embeddable
public class RecipeDetailsFormat {

    private int cookingTime;
    private int calories;
    private int servings;

    public RecipeDetailsFormat() {
    }

    public RecipeDetailsFormat(int cookingTime, int calories, int servings) {
        this.cookingTime = cookingTime;
        this.calories = calories;
        this.servings = servings;
    }

    public int getCookingTime() {
        return cookingTime;
    }

    public int getCalories() {
        return calories;
    }

    public int getServings() {
        return servings;
    }

}