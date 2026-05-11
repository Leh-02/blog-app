package com.example.blogapp.dto.user;

public record UserSummaryResponse(
        Long id,
        String fullName,
        String email,
        String role
) {
}
