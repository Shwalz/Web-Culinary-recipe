package com.example.cookbook.repository;

import com.example.cookbook.domain.Favorite;
import com.example.cookbook.domain.Recipe;
import com.example.cookbook.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {

    Optional<Favorite> findByUserUsernameAndRecipeId(String username, Long recipeId);
    List<Favorite> findByUser(User user);

}
