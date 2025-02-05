package com.example.cookbook.service;

import com.example.cookbook.domain.Role;
import com.example.cookbook.domain.User;
import com.example.cookbook.repository.RoleRepository;
import com.example.cookbook.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void register(User user) {
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        user.setActivationCode(generateActivationCode());

        user.setEnabled(false);

        Role userRole = roleRepository.findByType(Role.Types.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Role not found"));
        user.setRoles(Set.of(userRole));

        userRepository.save(user);
    }

    private String generateActivationCode() {
        return Long.toHexString(System.currentTimeMillis());
    }

    @Override
    @Transactional
    public User activateUser(String activationCode) {
        return userRepository.findByActivationCode(activationCode)
                .map(user -> {
                    user.setEnabled(true);
                    userRepository.flush();
                    user.setActivationCode(null); // Удаляем код после активации
                    return userRepository.save(user);
                })
                .orElseThrow(() -> new IllegalArgumentException("Nieprawidłowy kod aktywacyjny."));
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority(role.getType().name()))
                        .collect(Collectors.toList()))
                .accountLocked(false)
                .accountExpired(false)
                .credentialsExpired(false)
                .disabled(!user.isEnabled())
                .build();
    }

    @Override
    public boolean isEmailTaken(String email) {
        return userRepository.existsByEmail(email);
    }
}