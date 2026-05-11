package com.example.blogapp.controller;

import com.example.blogapp.dto.post.PostResponse;
import com.example.blogapp.dto.user.UserSummaryResponse;
import com.example.blogapp.service.PostService;
import com.example.blogapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;
    private final PostService postService;

    public UserController(UserService userService, PostService postService) {
        this.userService = userService;
        this.postService = postService;
    }

    @Operation(summary = "Get current authenticated user")
    @GetMapping("/me")
    public UserSummaryResponse getCurrentUser(Authentication authentication) {
        return userService.getCurrentUserProfile(authentication.getName());
    }

    @Operation(summary = "Get current user posts")
    @GetMapping("/me/posts")
    public java.util.List<PostResponse> getCurrentUserPosts(Authentication authentication) {
        return postService.getMyPosts(authentication.getName());
    }
}
