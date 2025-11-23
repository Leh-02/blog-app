package com.example.blogapp.config;

import com.example.blogapp.model.Post;
import com.example.blogapp.model.Role;
import com.example.blogapp.model.User;
import com.example.blogapp.repository.PostRepository;
import com.example.blogapp.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initData(
            UserRepository userRepository,
            PostRepository postRepository,
            PasswordEncoder encoder
    ) {
        return args -> {

            if (userRepository.count() > 0) return;

            // ADMIN
            User admin = new User();
            admin.setEmail("admin@example.com");
            admin.setFullName("Admin");
            admin.setPassword(encoder.encode("admin123"));
            admin.setRole(Role.ADMIN);
            userRepository.save(admin);

            // READER
            User reader = new User();
            reader.setEmail("reader@example.com");
            reader.setFullName("Reader");
            reader.setPassword(encoder.encode("reader123"));
            reader.setRole(Role.READER);
            userRepository.save(reader);

            // POSTS
            Post p1 = new Post(
                    "Welcome to BlogApp",
                    "This is the first post. Welcome!",
                    admin
            );

            Post p2 = new Post(
                    "Spring Boot + Thymeleaf",
                    "This demo shows a simple blog using Spring Boot and Thymeleaf.",
                    admin
            );

            postRepository.saveAll(List.of(p1, p2));
        };
    }
}
