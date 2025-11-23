package com.example.blogapp.controller;

import com.example.blogapp.model.User;
import com.example.blogapp.service.UserService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // --- LOGIN PAGE ---
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    // --- REGISTER PAGE ---
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    // --- PROCESS REGISTRATION ---
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {

        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "User with this email already exists");
            return "register";
        }

        userService.createUser(user);  // Service сам шифрує пароль і ставить роль READER
        return "redirect:/login?registered=true";
    }

    // --- REDIRECT BY ROLE AFTER LOGIN ---
    @GetMapping("/redirect-after-login")
    public String redirectAfterLogin() {
        String role = userService.getCurrentUserRole();

        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/user/home";
        }
    }
}
