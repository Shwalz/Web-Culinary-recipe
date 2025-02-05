package com.example.cookbook.domain;

import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Recipe name cannot be blank.")
    @Size(min = 3, max = 100, message = "Recipe name must be between {0} and {1} characters.")
    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    @NotEmpty(message = "Ingredients cannot be blank.")
    @Valid
    private List<Ingredient> ingredients = new ArrayList<>();



    @NotBlank(message = "Instructions cannot be blank.")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String instructions;

    @NotBlank(message = "Difficulty level is required.")
    private String difficultyLevel;

    @Min(value = 1, message = "Rating must be at least {0}.")
    @Max(value = 5, message = "Rating cannot exceed {0}.")
    @Column(nullable = false)
    private float rating;

    @Past(message = "Creation date must be in the past.")
    @Column(nullable = false)
    private LocalDate creationDate;

    @NotNull(message = "Specify if the recipe is vegetarian.")
    @Column(nullable = false)
    private Boolean vegetarian;

    @Embedded
    @Valid
    private RecipeDetailsFormat recipeDetails;

    @ManyToOne(optional = false)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category is required.")
    private Category category;

    @Column
    private String imagePath;

    @ManyToMany
    @JoinTable(
            name = "recipe_tags",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<Tag> tags = new HashSet<>();

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDateTime;

    @LastModifiedDate
    private LocalDateTime lastModifiedDateTime;

    @CreatedBy
    private String createdBy;

    @LastModifiedBy
    private String lastModifiedBy;

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "recipe", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Favorite> favorites = new HashSet<>();

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "nutritional_value_id")
    private NutritionalValue nutritionalValue;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "recipe_equipment",
            joinColumns = @JoinColumn(name = "recipe_id"),
            inverseJoinColumns = @JoinColumn(name = "equipment_id")
    )
    private Set<Equipment> equipment = new HashSet<>();



    public Set<Equipment> getEquipment() {
        return equipment;
    }

    public void setEquipment(Set<Equipment> equipment) {
        this.equipment = equipment;
    }
    public Recipe() {}

    public Recipe(Long id, String name, List<Ingredient> ingredients, String instructions, String difficultyLevel,
                  float rating, LocalDate creationDate, boolean vegetarian, RecipeDetailsFormat recipeDetails,
                  Category category, Set<Tag> tags) {
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.difficultyLevel = difficultyLevel;
        this.rating = rating;
        this.creationDate = creationDate;
        this.vegetarian = vegetarian;
        this.recipeDetails = recipeDetails;
        this.category = category;
        this.tags = tags;
    }

    public int getCalories() {
        return this.recipeDetails != null ? this.recipeDetails.getCalories() : 0;
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", difficultyLevel='" + difficultyLevel + '\'' +
                ", rating=" + rating +
                ", vegetarian=" + vegetarian +
                ", createdDateTime=" + createdDateTime +
                ", createdBy='" + createdBy + '\'' +
                '}';
    }
}
