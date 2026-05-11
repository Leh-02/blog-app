package com.example.blogapp;

import com.example.blogapp.model.Role;
import com.example.blogapp.model.User;
import com.example.blogapp.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.seed.admin-email}")
    private String adminEmail;

    @Value("${app.seed.admin-password}")
    private String adminPassword;

    @Value("${app.seed.admin-name}")
    private String adminName;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        userRepository.findByEmail(adminEmail.toLowerCase()).orElseGet(() -> {
            User admin = new User();
            admin.setFullName(adminName);
            admin.setEmail(adminEmail.toLowerCase());
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRole(Role.ADMIN);
            return userRepository.save(admin);
        });
    }
}
