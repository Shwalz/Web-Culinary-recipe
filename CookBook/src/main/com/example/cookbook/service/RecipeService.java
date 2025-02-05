package com.example.cookbook.service;

import com.example.cookbook.domain.Ingredient;
import com.example.cookbook.domain.NutritionalValue;
import com.example.cookbook.domain.Recipe;
import com.example.cookbook.repository.CategoryRepository;
import com.example.cookbook.repository.EquipmentRepository;
import com.example.cookbook.repository.RecipeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class RecipeService {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;
    private final EquipmentRepository equipmentRepository;
    private final String uploadDir = "src/main/resources/static/uploads/";

    public RecipeService(RecipeRepository recipeRepository, CategoryRepository categoryRepository, EquipmentRepository equipmentRepository) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
        this.equipmentRepository = equipmentRepository;
    }

    public void saveRecipe(Recipe recipe, MultipartFile image) {
        if (image != null && !image.isEmpty()) {
            try {
                String fileName = image.getOriginalFilename();
                if (fileName == null || fileName.isBlank()) {
                    throw new RuntimeException("Имя файла изображения пустое.");
                }

                Path uploadPath = Paths.get(uploadDir);
                log.debug("Uploading to path: {}", uploadPath.toAbsolutePath());

                Files.createDirectories(uploadPath);

                Path filePath = uploadPath.resolve(fileName);

                Files.write(filePath, image.getBytes());
                log.debug("File saved successfully at: {}", filePath.toAbsolutePath());

                recipe.setImagePath(fileName);
            } catch (IOException e) {
                log.error("Błąd podczas przesyłania pliku: {}", e.getMessage(), e);
                throw new RuntimeException("Błąd podczas ładowania obrazu: " + e.getMessage(), e);
            }
        } else {
            log.warn("Obraz nie został dostarczony.");
        }

        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.setRecipe(recipe);
        }

        recipeRepository.save(recipe);
    }

    public void updateRecipe(Long id, Recipe updatedRecipe, MultipartFile image) {
        Recipe recipe = getRecipeById(id);

        if (image != null && !image.isEmpty()) {
            try {
                String fileName = image.getOriginalFilename();
                if (fileName == null || fileName.isBlank()) {
                    throw new RuntimeException("Nazwa pliku obrazu jest pusta.");
                }

                Path uploadPath = Paths.get(uploadDir);
                log.debug("Updating image at path: {}", uploadPath.toAbsolutePath());

                Files.createDirectories(uploadPath);

                Path filePath = uploadPath.resolve(fileName);

                Files.write(filePath, image.getBytes());
                log.debug("File updated successfully at: {}", filePath.toAbsolutePath());

                recipe.setImagePath(fileName);
            } catch (IOException e) {
                log.error("Ошибка при загрузке файла: {}", e.getMessage(), e);
                throw new RuntimeException("Ошибка при загрузке изображения: " + e.getMessage(), e);
            }
        }

        recipe.setName(updatedRecipe.getName());
        recipe.setIngredients(updatedRecipe.getIngredients());
        recipe.setInstructions(updatedRecipe.getInstructions());
        recipe.setCategory(updatedRecipe.getCategory());
        recipe.setTags(updatedRecipe.getTags());
        recipe.setDifficultyLevel(updatedRecipe.getDifficultyLevel());
        recipe.setRating(updatedRecipe.getRating());
        recipe.setVegetarian(updatedRecipe.getVegetarian());

        for (Ingredient ingredient : recipe.getIngredients()) {
            ingredient.setRecipe(recipe);
        }

        recipeRepository.save(recipe);
    }

    public Recipe getRecipeById(Long id) {
        return recipeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Рецепт не найден с ID: " + id));
    }

    public void deleteRecipe(Long id) {
        Recipe recipe = getRecipeById(id);
        recipeRepository.delete(recipe);
    }

    public List<Recipe> findFilteredRecipes(String keyword, Long categoryId, Integer minCalories, Integer maxCalories, String startDate, String endDate, Boolean vegetarian) {
        List<Recipe> recipes = recipeRepository.findAll();

        if (keyword != null && !keyword.isEmpty()) {
            recipes = recipes.stream()
                    .filter(recipe -> recipe.getName().toLowerCase().contains(keyword.toLowerCase()))
                    .toList();
        }
        if (categoryId != null) {
            recipes = recipes.stream()
                    .filter(recipe -> recipe.getCategory().getId().equals(categoryId))
                    .toList();
        }
        if (minCalories != null) {
            recipes = recipes.stream()
                    .filter(recipe -> recipe.getCalories() >= minCalories)
                    .toList();
        }
        if (maxCalories != null) {
            recipes = recipes.stream()
                    .filter(recipe -> recipe.getCalories() <= maxCalories)
                    .toList();
        }
        if (startDate != null && !startDate.isEmpty()) {
            recipes = recipes.stream()
                    .filter(recipe -> recipe.getCreationDate().isAfter(LocalDate.parse(startDate)))
                    .toList();
        }
        if (endDate != null && !endDate.isEmpty()) {
            recipes = recipes.stream()
                    .filter(recipe -> recipe.getCreationDate().isBefore(LocalDate.parse(endDate)))
                    .toList();
        }
        if (vegetarian != null) {
            recipes = recipes.stream()
                    .filter(recipe -> recipe.getVegetarian().equals(vegetarian))
                    .toList();
        }

        return recipes;
    }

    public List<?> getCategories() {
        return categoryRepository.findAll();
    }

    public List<?> getEquipmentList() {
        return equipmentRepository.findAll();
    }

    public Recipe prepareNewRecipe() {
        Recipe recipe = new Recipe();
        recipe.getIngredients().add(new Ingredient());
        recipe.setNutritionalValue(new NutritionalValue());
        return recipe;
    }

    public Optional<Recipe> findById(Long id) {
        return recipeRepository.findById(id);
    }
}
