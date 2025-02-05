package com.example.cookbook.config;

import com.example.cookbook.domain.*;
import com.example.cookbook.repository.*;
import jakarta.transaction.Transactional;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.beans.factory.InitializingBean;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Configuration
public class DataInitializer {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ReviewRepository reviewRepository;
    private final FavoriteRepository favoriteRepository;
    private final NutritionalValueRepository nutritionalValueRepository;
    private final EquipmentRepository equipmentRepository;

    public DataInitializer(
            RecipeRepository recipeRepository,
            CategoryRepository categoryRepository,
            TagRepository tagRepository,
            UserRepository userRepository,
            RoleRepository roleRepository,
            PasswordEncoder passwordEncoder,
            ReviewRepository reviewRepository,
            FavoriteRepository favoriteRepository,
            NutritionalValueRepository nutritionalValueRepository,
            EquipmentRepository equipmentRepository
    ) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.reviewRepository = reviewRepository;
        this.favoriteRepository = favoriteRepository;
        this.nutritionalValueRepository = nutritionalValueRepository;
        this.equipmentRepository = equipmentRepository;
    }

    @Bean
    public InitializingBean initializeData() {
        return () -> {
            initializeRolesAndUsers();
            initializeDataWithTransactions();
        };
    }

    private void initializeRolesAndUsers() {
        if (roleRepository.count() == 0) {
            Role adminRole = roleRepository.save(new Role(Role.Types.ROLE_ADMIN));
            Role userRole = roleRepository.save(new Role(Role.Types.ROLE_USER));

            User admin = createUser("admin", "admin@example.com", "admin123", Set.of(adminRole));
            User user = createUser("user", "user@example.com", "password", Set.of(userRole));

            userRepository.saveAll(List.of(admin, user));
        }
    }

    private User createUser(String username, String email, String password, Set<Role> roles) {
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPasswordConfirm(user.getPassword());
        user.setEnabled(true);
        user.setRoles(roles);
        user.setActivationCode(generateActivationCode());
        return user;
    }

    @Transactional
    public void initializeDataWithTransactions() {
        if (categoryRepository.count() == 0) {
            initializeCategoriesAndRecipes();
        }
    }

    @Transactional
    public void initializeCategoriesAndRecipes() {
        Category breakfast = categoryRepository.save(new Category(null, "Śniadanie"));
        Category dinner = categoryRepository.save(new Category(null, "Kolacja"));

        Tag easy = tagRepository.save(new Tag(null, "Łagodny"));
        Tag spicy = tagRepository.save(new Tag(null, "Pikantny"));

        // Добавляем оборудование
        Equipment pan = equipmentRepository.save(new Equipment(null, "Patelnia"));
        Equipment mixer = equipmentRepository.save(new Equipment(null, "Mikser"));
        Equipment pot = equipmentRepository.save(new Equipment(null, "Garnek"));

        Recipe recipe1 = createRecipe("Naleśniki", "Wymieszać składniki i usmażyć.", "Trudny", 5.0f,
                LocalDate.now().minusDays(1), true, breakfast, List.of(
                        new Ingredient("Mąka", 200.0, "g"),
                        new Ingredient("Mleko", 300.0, "ml"),
                        new Ingredient("Jajka", 2.0, "pcs")
                ), Set.of(easy), "pancakes.jpg", new RecipeDetailsFormat(30, 400, 5), Set.of(mixer));

        Recipe recipe2 = createRecipe("Pierogi", "Zagotować i podawać z masłem.", "Średni", 3.0f,
                LocalDate.now().minusWeeks(1), false, dinner, List.of(
                        new Ingredient("Mąka", 300.0, "g"),
                        new Ingredient("Woda", 100.0, "ml"),
                        new Ingredient("Sol", 1.0, "tsp")
                ), Set.of(spicy), "pierogi.jpg", new RecipeDetailsFormat(30, 400, 5), Set.of(pot));

        Recipe recipe3 = createRecipe("Jajecznica", "Roztrzepać jajka i usmażyć na maśle.", "Łatwy", 2.5f,
                LocalDate.now().minusDays(3), true, breakfast, List.of(
                        new Ingredient("Jajka", 3.0, "pcs"),
                        new Ingredient("Masło", 20.0, "g"),
                        new Ingredient("Sol", 1.0, "pinch")
                ), Set.of(easy), "scrambled_eggs.jpg", new RecipeDetailsFormat(30, 400, 5), Set.of(pan));

        recipe1.setNutritionalValue(new NutritionalValue(null, 400, 10.0, 5.0, 50.0));
        recipe2.setNutritionalValue(new NutritionalValue(null, 250, 8.0, 3.5, 30.0));
        recipe3.setNutritionalValue(new NutritionalValue(null, 200, 6.0, 4.0, 20.0));

        System.out.println("Saving recipe: " + recipe1.getName() + " with equipment: " + recipe1.getEquipment());
        recipeRepository.saveAll(List.of(recipe1, recipe2, recipe3));

        // Добавляем отзывы для рецептов
        createReview("Świetne naleśniki, bardzo smaczne!", 5, recipe1, "user");
        createReview("Trochę mdły, ale ogólnie niezły.", 3, recipe2, "user");
        createReview("Idealne na śniadanie!", 4, recipe3, "user");
    }

    private void createReview(String content, int rating, Recipe recipe, String username) {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new RuntimeException("User not found"));
        Review review = new Review();
        review.setContent(content);
        review.setRating(rating);
        review.setRecipe(recipe);
        review.setUser(user);
        reviewRepository.save(review);
    }

    private Recipe createRecipe(String name, String instructions, String difficultyLevel,
                                float rating, LocalDate creationDate, boolean vegetarian,
                                Category category, List<Ingredient> ingredients, Set<Tag> tags,
                                String imagePath, RecipeDetailsFormat recipeDetails, Set<Equipment> equipment) {
        Recipe recipe = new Recipe();
        recipe.setName(name);
        recipe.setInstructions(instructions);
        recipe.setDifficultyLevel(difficultyLevel);
        recipe.setRating(rating);
        recipe.setCreationDate(creationDate);
        recipe.setVegetarian(vegetarian);
        recipe.setCategory(category);
        recipe.setTags(tags);
        recipe.setImagePath(imagePath);
        recipe.setRecipeDetails(recipeDetails);
        recipe.setEquipment(equipment);

        for (Ingredient ingredient : ingredients) {
            ingredient.setRecipe(recipe);
        }
        recipe.setIngredients(ingredients);

        return recipe;
    }


    private String generateActivationCode() {
        return Long.toHexString(System.currentTimeMillis());
    }
}
