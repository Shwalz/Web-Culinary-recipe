package com.example.cookbook.service;

import com.example.cookbook.domain.Recipe;
import com.example.cookbook.domain.Review;
import com.example.cookbook.domain.User;
import com.example.cookbook.repository.RecipeRepository;
import com.example.cookbook.repository.ReviewRepository;
import com.example.cookbook.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.List;

@Service
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public ReviewService(ReviewRepository reviewRepository, UserRepository userRepository, RecipeRepository recipeRepository) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    public List<Review> findByRecipeId(Long recipeId) {
        return reviewRepository.findByRecipeId(recipeId);
    }

    public Review saveReview(Review review, Long recipeId, Principal principal) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new RuntimeException("Recipe not found"));

        User user = getUserByPrincipal(principal);

        review.setId(null);
        review.setRecipe(recipe);
        review.setUser(user);
        reviewRepository.save(review);

        updateRecipeRating(recipe);

        return review;
    }

    public User getUserByPrincipal(Principal principal) {
        return userRepository.findByUsername(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found: " + principal.getName()));
    }

    private void updateRecipeRating(Recipe recipe) {
        List<Review> reviews = reviewRepository.findByRecipeId(recipe.getId());

        if (reviews.isEmpty()) {
            recipe.setRating(0);
        } else {
            double averageRating = reviews.stream()
                    .mapToDouble(Review::getRating)
                    .average()
                    .orElse(0);
            recipe.setRating(Math.round(averageRating * 10) / 10.0f);
        }

        recipeRepository.save(recipe);
    }
}
