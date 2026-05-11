package com.example.blogapp.dto.auth;

import com.example.blogapp.dto.user.UserSummaryResponse;

public record AuthResponse(
        String token,
        String tokenType,
        long expiresIn,
        UserSummaryResponse user
) {
}
