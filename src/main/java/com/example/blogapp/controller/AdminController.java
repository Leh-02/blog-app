package com.example.blogapp.controller;

import com.example.blogapp.dto.user.UserSummaryResponse;
import com.example.blogapp.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final UserService userService;

    public AdminController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Get all users (ADMIN only)")
    @GetMapping("/users")
    public List<UserSummaryResponse> getAllUsers() {
        return userService.getAllUsers();
    }
}
