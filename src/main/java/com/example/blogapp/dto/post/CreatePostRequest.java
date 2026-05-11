package com.example.blogapp.dto.post;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CreatePostRequest(
        @NotBlank(message = "Title is required")
        @Size(max = 180, message = "Title is too long")
        String title,

        @NotBlank(message = "Content is required")
        String content
) {
}
