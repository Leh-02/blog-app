package com.example.blogapp.dto.comment;

import com.example.blogapp.dto.user.UserSummaryResponse;

import java.time.LocalDateTime;

public record CommentResponse(
        Long id,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UserSummaryResponse author
) {
}
