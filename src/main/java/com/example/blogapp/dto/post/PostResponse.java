package com.example.blogapp.dto.post;

import com.example.blogapp.dto.comment.CommentResponse;
import com.example.blogapp.dto.user.UserSummaryResponse;

import java.time.LocalDateTime;
import java.util.List;

public record PostResponse(
        Long id,
        String title,
        String content,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        UserSummaryResponse author,
        String imageUrl,
        String imageOriginalName,
        String imageContentType,
        Long imageSize,
        int likeCount,
        int saveCount,
        int commentCount,
        List<CommentResponse> comments
) {
}
