package com.example.cookbook.service;

import com.example.cookbook.domain.User;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    void register(User user);

    User activateUser(String code);
    boolean isEmailTaken(String email);
}