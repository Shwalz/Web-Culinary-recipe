package com.example.cookbook.validation;

import com.example.cookbook.domain.Recipe;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

@Component
public class CustomValidator implements Validator {

    @Override
    public boolean supports(Class<?> clazz) {
        return Recipe.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        Recipe recipe = (Recipe) target;

        if (recipe.getName() != null && recipe.getName().equalsIgnoreCase(recipe.getDifficultyLevel())) {
            errors.rejectValue("difficultyLevel", "difficultyLevel.match", "Nazwa i poziom trudności nie mogą być takie same.");
        }
    }
}