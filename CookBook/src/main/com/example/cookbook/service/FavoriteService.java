package com.example.cookbook.service;

import com.example.cookbook.domain.Favorite;
import com.example.cookbook.domain.Recipe;
import com.example.cookbook.domain.User;
import com.example.cookbook.repository.FavoriteRepository;
import com.example.cookbook.repository.RecipeRepository;
import com.example.cookbook.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;

    public FavoriteService(FavoriteRepository favoriteRepository, UserRepository userRepository, RecipeRepository recipeRepository) {
        this.favoriteRepository = favoriteRepository;
        this.userRepository = userRepository;
        this.recipeRepository = recipeRepository;
    }

    public List<Recipe> findFavoritesByUser(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return favoriteRepository.findByUser(user).stream()
                .map(Favorite::getRecipe)
                .toList();
    }

    public Map<Long, Boolean> getFavoriteStatusForUser(Principal principal, List<Recipe> recipes) {
        Map<Long, Boolean> favoriteStatus = new HashMap<>();
        if (principal != null) {
            String username = principal.getName();
            for (Recipe recipe : recipes) {
                boolean isFavorite = isFavoriteForUser(username, recipe.getId());
                favoriteStatus.put(recipe.getId(), isFavorite);
            }
        }
        return favoriteStatus;
    }

    public boolean isFavoriteForUser(String username, Long recipeId) {
        return favoriteRepository.findByUserUsernameAndRecipeId(username, recipeId).isPresent();
    }

    public void toggleFavorite(String username, Long recipeId) {
        Optional<Favorite> existingFavorite = favoriteRepository.findByUserUsernameAndRecipeId(username, recipeId);
        if (existingFavorite.isPresent()) {
            favoriteRepository.delete(existingFavorite.get());
        } else {
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Recipe recipe = recipeRepository.findById(recipeId)
                    .orElseThrow(() -> new RuntimeException("Recipe not found"));
            Favorite favorite = new Favorite(recipe, user);
            favoriteRepository.save(favorite);
        }
    }
}
