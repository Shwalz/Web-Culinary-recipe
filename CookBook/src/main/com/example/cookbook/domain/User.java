package com.example.cookbook.domain;

import com.example.cookbook.validation.RegistrationValidation;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Username cannot be blank.")
    @Size(min = 4, max = 36, message = "Username must be between {0} and {1} characters.")
    @Column(nullable = false, unique = true)
    private String username;

    @NotBlank(message = "Password cannot be blank.")
    @Size(min = 6, message = "Password must have at least {0} characters.")
    private String password;

    @NotBlank(message = "Password confirmation cannot be blank.", groups = RegistrationValidation.class)
    @Size(min = 6, message = "Password must have at least {0} characters.", groups = RegistrationValidation.class)
    @Transient
    private String passwordConfirm;

    @Email(message = "Please provide a valid email address.")
    @NotBlank(message = "Email cannot be blank.")
    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "activation_code", nullable = true)
    private String activationCode;

    private boolean enabled = false;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Review> reviews = new HashSet<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<Favorite> favorites = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "users_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    public User(String username, String email, String activationCode) {
        this.username = username;
        this.email = email;
        this.activationCode = activationCode;
        this.enabled = false;
    }

    public User(String username) {
        this(username, null, null);
    }

    public User(String username, boolean enabled) {
        this(username, null, null);
        this.enabled = enabled;
    }

    public String getActivationCode() {
        return activationCode;
    }

    public void setActivationCode(String activationCode) {
        this.activationCode = activationCode;
    }
}
