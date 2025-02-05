package com.example.cookbook.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Ingredient name cannot be blank.")
    @Size(min = 2, max = 100, message = "Ingredient name must be between {0} and {1} characters.")
    @Column(nullable = false)
    private String name;

    @Min(value = 1, message = "Ingredient quantity must be greater than 0.")
    @Column(nullable = false)
    private double quantity;

    @NotBlank(message = "Unit of measurement cannot be blank.")
    @Size(min = 1, max = 20, message = "Unit of measurement cannot exceed {0} characters.")
    @Column(nullable = false)
    private String unit;

    @ManyToOne(optional = false)
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    public Ingredient(String name, double quantity, String unit) {
        this.name = name;
        this.quantity = quantity;
        this.unit = unit;
    }
}
