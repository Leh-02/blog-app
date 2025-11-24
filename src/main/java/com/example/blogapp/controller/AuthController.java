package com.example.blogapp.controller;

import com.example.blogapp.model.User;
import com.example.blogapp.service.UserService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    // LOGIN PAGE
    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    //REGISTER PAGE
    @GetMapping("/register")
    public String showRegisterPage(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    //PROCESS REGISTRATION
    @PostMapping("/register")
    public String registerUser(@ModelAttribute("user") User user, Model model) {

        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "User with this email already exists");
            return "register";
        }

        userService.createUser(user);

        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword(),
                        List.of(new SimpleGrantedAuthority("ROLE_READER"))
                );

        SecurityContextHolder.getContext().setAuthentication(auth);
        return "redirect:/reader/home";
    }

    // REDIRECT BY ROLE AFTER LOGIN
    @GetMapping("/redirect-after-login")
    public String redirectAfterLogin() {
        String role = userService.getCurrentUserRole();

        if (role.equals("ROLE_ADMIN")) {
            return "redirect:/admin/dashboard";
        } else {
            return "redirect:/reader/home";
        }
    }
}
