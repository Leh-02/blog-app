package com.example.blogapp.service;

import com.example.blogapp.dto.auth.AuthResponse;
import com.example.blogapp.dto.auth.LoginRequest;
import com.example.blogapp.dto.auth.RegisterRequest;
import com.example.blogapp.dto.user.UserSummaryResponse;
import com.example.blogapp.exception.BadRequestException;
import com.example.blogapp.model.Role;
import com.example.blogapp.model.User;
import com.example.blogapp.repository.UserRepository;
import com.example.blogapp.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserService userService;
    private final AsyncNotificationService asyncNotificationService;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager,
                       JwtService jwtService,
                       UserService userService,
                       AsyncNotificationService asyncNotificationService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.userService = userService;
        this.asyncNotificationService = asyncNotificationService;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new BadRequestException("User with this email already exists");
        }

        User user = new User();
        user.setFullName(request.fullName().trim());
        user.setEmail(normalizedEmail);
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setRole(Role.READER);

        User savedUser = userRepository.save(user);
        asyncNotificationService.onUserRegistered(savedUser);

        return buildAuthResponse(savedUser);
    }

    public AuthResponse login(LoginRequest request) {
        String normalizedEmail = request.email().trim().toLowerCase();
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(normalizedEmail, request.password())
        );

        User user = userRepository.findByEmail(normalizedEmail)
                .orElseThrow(() -> new BadRequestException("User not found"));

        return buildAuthResponse(user);
    }

    private AuthResponse buildAuthResponse(User user) {
        String token = jwtService.generateToken(user);
        UserSummaryResponse userResponse = userService.toSummary(user);

        return new AuthResponse(
                token,
                "Bearer",
                jwtService.getExpirationMs(),
                userResponse
        );
    }
}
