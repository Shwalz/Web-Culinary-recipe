package com.example.cookbook.utils;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RecipeFilter {
    private String phrase;
    private Integer minCalories;
    private Integer maxCalories;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean vegetarian;
}