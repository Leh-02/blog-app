package com.example.blogapp.controller;

import com.example.blogapp.model.Post;
import com.example.blogapp.repository.CommentRepository;
import com.example.blogapp.repository.UserRepository;
import com.example.blogapp.service.PostService;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final PostService postService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public AdminController(PostService postService,
                           UserRepository userRepository,
                           CommentRepository commentRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    // DASHBOARD
    @GetMapping("/dashboard")
    public String dashboard() {
        return "admin/admin_dashboard";
    }

    //POSTS LIST
    @GetMapping("/posts")
    public String adminPosts(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "admin/admin_posts";
    }

    // CREATE POST FORM
    @GetMapping("/posts/new")
    public String newPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "admin/admin_posts_form";
    }

    // SAVE POST
    @PostMapping("/posts")
    public String createPost(
            @Valid @ModelAttribute("post") Post post,
            BindingResult br,
            @AuthenticationPrincipal UserDetails userDetails) {

        if (br.hasErrors()) {
            return "admin/admin_posts_form";
        }

        if (userDetails != null) {
            userRepository.findByEmail(userDetails.getUsername())
                    .ifPresent(post::setAuthor);
        }

        postService.save(post);
        return "redirect:/admin/posts";
    }

    //EDIT POST FORM
    @GetMapping("/posts/edit/{id}")
    public String editPost(@PathVariable Long id, Model model) {
        Optional<Post> post = postService.findById(id);
        if (post.isEmpty()) return "redirect:/admin/posts";

        model.addAttribute("post", post.get());
        return "admin/admin_posts_form";
    }

    // DELETE POST
    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.delete(id);
        return "redirect:/admin/posts";
    }

    // DELETE COMMENT
    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long id) {
        commentRepository.deleteById(id);
        return "redirect:/admin/posts";
    }

    // USERS LIST
    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userRepository.findAll());
        return "admin/admin_users";
    }

    // DELETE USER
    @PostMapping("/users/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userRepository.deleteById(id);
        return "redirect:/admin/users";
    }
}
