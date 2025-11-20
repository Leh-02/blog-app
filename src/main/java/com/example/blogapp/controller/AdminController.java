package com.example.blogapp.controller;

import com.example.blogapp.model.Post;
import com.example.blogapp.model.User;
import com.example.blogapp.repository.CommentRepository;
import com.example.blogapp.repository.UserRepository;
import com.example.blogapp.service.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.Optional;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final PostService postService;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public AdminController(PostService postService, UserRepository userRepository, CommentRepository commentRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    @GetMapping("/posts")
    public String adminPosts(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "admin_posts";
    }

    @GetMapping("/posts/new")
    public String newPostForm(Model model) {
        model.addAttribute("post", new Post());
        return "post_form";
    }

    @PostMapping("/posts")
    public String createPost(@Valid Post post, BindingResult br, @AuthenticationPrincipal UserDetails userDetails) {
        if (br.hasErrors()) return "post_form";
        if (userDetails != null) {
            Optional<User> ou = userRepository.findByEmail(userDetails.getUsername());
            ou.ifPresent(post::setAuthor);
        }
        postService.save(post);
        return "redirect:/admin/posts";
    }

    @GetMapping("/posts/edit/{id}")
    public String editForm(@PathVariable Long id, Model model) {
        Optional<Post> op = postService.findById(id);
        if (op.isEmpty()) return "redirect:/admin/posts";
        model.addAttribute("post", op.get());
        return "post_form";
    }

    @PostMapping("/posts/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.delete(id);
        return "redirect:/admin/posts";
    }

    @PostMapping("/comments/{id}/delete")
    public String deleteComment(@PathVariable Long id) {
        commentRepository.deleteById(id);
        return "redirect:/admin/posts";
    }
}
