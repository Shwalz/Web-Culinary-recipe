package com.example.cookbook.domain;

public class IngredientFormat {

    private String ingredientName;
    private double quantity;
    private String unit;

    public IngredientFormat() {
    }

    public IngredientFormat(String ingredientName, double quantity, String unit) {
        this.ingredientName = ingredientName;
        this.quantity = quantity;
        this.unit = unit;
    }

    public String getIngredientName() {
        return ingredientName;
    }

    public double getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    @Override
    public String toString() {
        return ingredientName + " : " + quantity + " " + unit;
    }
}