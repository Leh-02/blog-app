package com.example.blogapp.controller;

import com.example.blogapp.model.Comment;
import com.example.blogapp.model.Post;
import com.example.blogapp.model.User;
import com.example.blogapp.repository.UserRepository;
import com.example.blogapp.service.PostService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final UserRepository userRepository;

    public PostController(PostService postService,
                          UserRepository userRepository) {
        this.postService = postService;
        this.userRepository = userRepository;
    }

    @GetMapping({"", "/"})
    public String list(Model model) {
        model.addAttribute("posts", postService.findAll());
        return "posts";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model) {
        Optional<Post> p = postService.findById(id);
        if (p.isEmpty()) return "redirect:/posts";
        model.addAttribute("post", p.get());
        model.addAttribute("comment", new Comment());
        return "reader/user_post_detail";
    }

    @PostMapping("/{id}/comment")
    public String comment(@PathVariable Long id,
                          @ModelAttribute Comment comment,
                          @AuthenticationPrincipal UserDetails userDetails) {
        Optional<Post> p = postService.findById(id);
        if (p.isEmpty()) return "redirect:/posts";

        if (userDetails == null) return "redirect:/login";

        userRepository.findByEmail(userDetails.getUsername()).ifPresent(user ->
                postService.addComment(p.get(), user, comment.getContent())
        );

        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/like")
    public String like(@PathVariable Long id,
                       @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) return "redirect:/login";

        Optional<Post> op = postService.findById(id);
        Optional<User> ou = userRepository.findByEmail(userDetails.getUsername());
        if (op.isPresent() && ou.isPresent()) {
            Post post = op.get();
            User user = ou.get();

            if (post.getLikedBy().contains(user)) {
                post.getLikedBy().remove(user);
                user.getLikedPosts().remove(post);
            } else {
                post.getLikedBy().add(user);
                user.getLikedPosts().add(post);
            }

            postService.save(post);
            userRepository.save(user);
        }
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/save")
    public String save(@PathVariable Long id,
                       @AuthenticationPrincipal UserDetails userDetails) {

        if (userDetails == null) return "redirect:/login";

        Optional<Post> op = postService.findById(id);
        Optional<User> ou = userRepository.findByEmail(userDetails.getUsername());
        if (op.isPresent() && ou.isPresent()) {
            Post post = op.get();
            User user = ou.get();

            if (user.getSavedPosts().contains(post)) {
                user.getSavedPosts().remove(post);
            } else {
                user.getSavedPosts().add(post);
            }
            userRepository.save(user);
        }
        return "redirect:/posts/" + id;
    }

    @GetMapping("/saved")
    public String saved(@AuthenticationPrincipal UserDetails userDetails,
                        Model model) {
        if (userDetails == null) return "redirect:/login";
        userRepository.findByEmail(userDetails.getUsername()).ifPresent(user ->
                model.addAttribute("posts", user.getSavedPosts())
        );
        return "posts";
    }
}
