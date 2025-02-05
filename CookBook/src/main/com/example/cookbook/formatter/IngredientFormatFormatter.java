package com.example.cookbook.formatter;

import com.example.cookbook.domain.IngredientFormat;
import org.springframework.format.Formatter;
import org.springframework.stereotype.Component;

import java.text.ParseException;
import java.util.Locale;

@Component
public class IngredientFormatFormatter implements Formatter<IngredientFormat> {

    @Override
    public IngredientFormat parse(String text, Locale locale) throws ParseException {
        String[] parts = text.split(" : ");
        if (parts.length != 3) {
            throw new ParseException("Nieprawidłowy format. Oczekiwano: nazwa : ilość jednostka", 0);
        }

        String name = parts[0].trim();
        double quantity;
        String unit;

        try {
            quantity = Double.parseDouble(parts[1].trim());
        } catch (NumberFormatException e) {
            throw new ParseException("Ilość musi być liczbą.", 0);
        }

        unit = parts[2].trim();
        return new IngredientFormat(name, quantity, unit);
    }

    @Override
    public String print(IngredientFormat ingredient, Locale locale) {
        return String.format("%s : %.2f %s", ingredient.getIngredientName(), ingredient.getQuantity(), ingredient.getUnit());
    }
}