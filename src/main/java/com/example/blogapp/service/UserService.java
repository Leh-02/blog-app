package com.example.blogapp.service;

import com.example.blogapp.dto.user.UserSummaryResponse;
import com.example.blogapp.exception.ResourceNotFoundException;
import com.example.blogapp.model.User;
import com.example.blogapp.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email.toLowerCase())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    public UserSummaryResponse getCurrentUserProfile(String email) {
        return toSummary(getByEmail(email));
    }

    public List<UserSummaryResponse> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(this::toSummary)
                .toList();
    }

    public UserSummaryResponse toSummary(User user) {
        return new UserSummaryResponse(
                user.getId(),
                user.getFullName(),
                user.getEmail(),
                user.getRole().name()
        );
    }
}
