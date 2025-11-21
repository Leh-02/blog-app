package com.example.blogapp.config;

import com.example.blogapp.model.Role;
import com.example.blogapp.model.User;
import com.example.blogapp.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    // ------------- LOAD USER BY EMAIL -------------
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return org.springframework.security.core.userdetails.User
                    .builder()
                    .username(user.getEmail())              // логін через email
                    .password(user.getPassword())           // пароль
                    .authorities(user.getRole().name())     // ADMIN або READER
                    .build();
        };
    }

    // ------------- PASSWORD ENCODER -------------
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // ------------- SECURITY RULES -------------
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // дозволяємо POST без token (для простоти)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/", "/login", "/register",
                                "/css/**", "/js/**", "/images/**"
                        ).permitAll()

                        // адмін панель
                        .requestMatchers("/admin/**").hasAuthority("ADMIN")

                        // решта — лише авторизовані
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login")                // своя сторінка логіну
                        .loginProcessingUrl("/login")       // URL куди відправляє форма
                        .defaultSuccessUrl("/posts", true)  // куди переходить після логіну
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .permitAll()
                );

        return http.build();
    }
}
