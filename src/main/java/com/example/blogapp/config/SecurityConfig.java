package com.example.blogapp.config;

import com.example.blogapp.model.User;
import com.example.blogapp.repository.UserRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;

    public SecurityConfig(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    //USER DETAILS
    @Bean
    public UserDetailsService userDetailsService() {
        return email -> {
            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found"));

            return org.springframework.security.core.userdetails.User
                    .builder()
                    .username(user.getEmail())
                    .password(user.getPassword())
                    .authorities("ROLE_" + user.getRole().name())
                    .build();
        };
    }

    // PASSWORD ENCODER
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //SUCCESS HANDLER
    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (HttpServletRequest request,
                HttpServletResponse response,
                Authentication auth) -> {

            boolean isAdmin = auth.getAuthorities()
                    .stream()
                    .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

            if (isAdmin) {
                response.sendRedirect("/admin/dashboard");
            } else {
                response.sendRedirect("/reader/home");
            }
        };
    }

    // SECURITY FILTERS
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .headers(headers -> headers.frameOptions(frame -> frame.disable()))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll()
                        .requestMatchers("/", "/login", "/register",
                                "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/admin/**").hasAuthority("ROLE_ADMIN")
                        .anyRequest().authenticated()
                )

                .formLogin(login -> login
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler())
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
