package com.example.cookbook.controller;

import com.example.cookbook.domain.Equipment;
import com.example.cookbook.domain.Recipe;
import com.example.cookbook.domain.Review;
import com.example.cookbook.service.FavoriteService;
import com.example.cookbook.service.RecipePdfService;
import com.example.cookbook.service.RecipeService;
import com.example.cookbook.service.ReviewService;
import jakarta.validation.Valid;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.validation.BindingResult;

import java.io.IOException;
import java.security.Principal;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Controller
@Log4j2
@RequestMapping("/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final ReviewService reviewService;
    private final FavoriteService favoriteService;
    private final RecipePdfService recipePdfService;

    public RecipeController(RecipeService recipeService, ReviewService reviewService,
                            FavoriteService favoriteService, RecipePdfService recipePdfService) {
        this.recipeService = recipeService;
        this.reviewService = reviewService;
        this.favoriteService = favoriteService;
        this.recipePdfService = recipePdfService;
    }

    @GetMapping
    public String listRecipes(
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "categoryId", required = false) Long categoryId,
            @RequestParam(value = "minCalories", required = false) Integer minCalories,
            @RequestParam(value = "maxCalories", required = false) Integer maxCalories,
            @RequestParam(value = "startDate", required = false) String startDate,
            @RequestParam(value = "endDate", required = false) String endDate,
            @RequestParam(value = "vegetarian", required = false) Boolean vegetarian,
            Model model, Principal principal) {

        List<Recipe> recipes = recipeService.findFilteredRecipes(keyword, categoryId, minCalories, maxCalories, startDate, endDate, vegetarian);
        model.addAttribute("recipes", recipes);
        model.addAttribute("favoriteStatus", favoriteService.getFavoriteStatusForUser(principal, recipes));

        return "recipes";
    }

    @GetMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addRecipeForm(Model model) {
        Recipe recipe = recipeService.prepareNewRecipe();

        model.addAttribute("recipe", recipe);
        model.addAttribute("categories", recipeService.getCategories());
        model.addAttribute("equipmentList", recipeService.getEquipmentList());
        return "addRecipe";
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('ADMIN')")
    public String addRecipe(@ModelAttribute("recipe") @Valid Recipe recipe,
                            BindingResult result,
                            @RequestParam(value = "image", required = false) MultipartFile image,
                            Model model) {
        if (result.hasErrors()) {
            return "addRecipe";
        }

        log.debug("Uploading file: {}", image != null ? image.getOriginalFilename() : "No file uploaded");

        recipeService.saveRecipe(recipe, image);

        return "redirect:/recipes";
    }

    @GetMapping("/{id}")
    public String recipeDetails(@PathVariable Long id, Model model, Principal principal) {
        Recipe recipe = recipeService.getRecipeById(id);
        boolean isFavorite = principal != null && favoriteService.isFavoriteForUser(principal.getName(), id);
        Set<Equipment> equipmentSet = recipe.getEquipment() != null ? recipe.getEquipment() : new HashSet<>();

        model.addAttribute("recipe", recipe);
        model.addAttribute("reviews", reviewService.findByRecipeId(id));
        model.addAttribute("isFavorite", isFavorite);
        model.addAttribute("newReview", new Review());
        model.addAttribute("equipmentList", equipmentSet);

        return "recipeDetails";
    }

    @PostMapping("/{id}/review")
    @PreAuthorize("isAuthenticated()")
    public String addReview(
            @PathVariable Long id,
            @Valid @ModelAttribute("newReview") Review review,
            BindingResult result,
            Principal principal,
            Model model) {

        if (result.hasErrors()) {
            Recipe recipe = recipeService.getRecipeById(id);
            List<Review> reviews = reviewService.findByRecipeId(id);

            model.addAttribute("recipe", recipe);
            model.addAttribute("reviews", reviews);
            model.addAttribute("newReview", review);
            return "recipeDetails";
        }

        reviewService.saveReview(review, id, principal);
        return "redirect:/recipes/" + id;
    }

    @GetMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String editRecipeForm(@PathVariable Long id, Model model) {
        model.addAttribute("recipe", recipeService.getRecipeById(id));
        model.addAttribute("categories", recipeService.getCategories());
        model.addAttribute("equipmentList", recipeService.getEquipmentList());
        return "editRecipe";
    }

    @PostMapping("/edit/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String updateRecipe(@PathVariable Long id,
                               @Valid @ModelAttribute Recipe recipe,
                               BindingResult result,
                               @RequestParam("file") MultipartFile file,
                               Model model) throws IOException {
        if (result.hasErrors()) {
            return "editRecipe";
        }

        recipeService.updateRecipe(id, recipe, file);
        return "redirect:/recipes";
    }

    @GetMapping("/delete/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return "redirect:/recipes";
    }

    @PostMapping("/{id}/favorite")
    @PreAuthorize("isAuthenticated()")
    public String toggleFavorite(@PathVariable Long id, Principal principal) {
        favoriteService.toggleFavorite(principal.getName(), id);
        return "redirect:/recipes/" + id;
    }

    @GetMapping("/favorites")
    @PreAuthorize("isAuthenticated()")
    public String listFavorites(Principal principal, Model model) {
        List<Recipe> favoriteRecipes = favoriteService.findFavoritesByUser(principal.getName());
        model.addAttribute("recipes", favoriteRecipes);
        return "favorites";
    }

    @GetMapping(value = "/{id}/download", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<ByteArrayResource> downloadRecipePdf(@PathVariable Long id) {
        Optional<Recipe> recipeOptional = recipeService.findById(id);
        if (recipeOptional.isPresent()) {
            Recipe recipe = recipeOptional.get();
            byte[] pdfBytes = recipePdfService.generateRecipePdf(recipe);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=recipe_" + id + ".pdf")
                    .contentType(MediaType.APPLICATION_PDF)
                    .body(new ByteArrayResource(pdfBytes));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
