package com.example.cookbook.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty
    @Size(min = 10, max = 500)
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Min(value = 1, message = "Rating must be at least {0}.")
    @Max(value = 5, message = "Rating cannot exceed {0}.")
    @Column(nullable = false)
    private int rating;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdDateTime;

    @ManyToOne
    @JoinColumn(name = "recipe_id", nullable = false)
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Review(String content, int rating, Recipe recipe, User user) {
        this.content = content;
        this.rating = rating;
        this.recipe = recipe;
        this.user = user;
    }
}
