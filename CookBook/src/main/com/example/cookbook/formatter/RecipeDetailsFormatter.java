package com.example.cookbook.formatter;

import com.example.cookbook.domain.RecipeDetailsFormat;
import org.springframework.format.Formatter;

import java.text.ParseException;
import java.util.Locale;

public class RecipeDetailsFormatter implements Formatter<RecipeDetailsFormat> {

    @Override
    public RecipeDetailsFormat parse(String text, Locale locale) throws ParseException {
        String[] parts = text.split(",");
        if (parts.length != 3) {
            throw new ParseException("Nieprawidłowy format szczegółów przepisu. Oczekiwano: czas gotowania, kaloryczność, porcje", 0);
        }

        try {
            int cookingTime = Integer.parseInt(parts[0].trim().replace("min", "").trim());
            int calories = Integer.parseInt(parts[1].trim().replace("kcal", "").trim());
            int servings = Integer.parseInt(parts[2].trim().replace("servings", "").trim());

            return new RecipeDetailsFormat(cookingTime, calories, servings);
        } catch (NumberFormatException e) {
            throw new ParseException("Nieprawidłowe dane liczbowe w szczegółach przepisu.", 0);
        }
    }

    @Override
    public String print(RecipeDetailsFormat details, Locale locale) {
        return String.format("%d min, %d kcal, %d servings", details.getCookingTime(), details.getCalories(), details.getServings());
    }
}