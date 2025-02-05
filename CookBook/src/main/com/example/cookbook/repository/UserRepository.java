package com.example.cookbook.repository;

import com.example.cookbook.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    Optional<User> findByActivationCode(String activationCode);  // Новый метод для поиска по активационному коду
    boolean existsByEmail(String email);
}