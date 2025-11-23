package com.example.blogapp.controller;

import com.example.blogapp.service.PostService;
import com.example.blogapp.repository.UserRepository;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/reader")
public class ReaderController {

    private final PostService postService;
    private final UserRepository userRepository;

    public ReaderController(PostService postService, UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
    }

    @GetMapping("/home")
    public String home(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "reader/user_posts";
    }

    @GetMapping("/saved")
    public String saved(
            @AuthenticationPrincipal UserDetails userDetails,
            Model model) {

        userRepository.findByEmail(userDetails.getUsername())
                .ifPresent(user -> model.addAttribute("posts", user.getSavedPosts()));

        return "reader/user_saved";
    }
}
