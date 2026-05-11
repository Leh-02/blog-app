package com.example.blogapp.controller;

import com.example.blogapp.dto.comment.CommentRequest;
import com.example.blogapp.dto.comment.CommentResponse;
import com.example.blogapp.dto.post.CreatePostRequest;
import com.example.blogapp.dto.post.PostResponse;
import com.example.blogapp.service.CommentService;
import com.example.blogapp.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @Operation(summary = "Get all posts")
    @GetMapping
    public List<PostResponse> getAllPosts() {
        return postService.getAllPosts();
    }

    @Operation(summary = "Get post by id")
    @GetMapping("/{postId}")
    public PostResponse getPostById(@PathVariable Long postId) {
        return postService.getPostById(postId);
    }


    @Operation(summary = "Create a post with optional image")
    @PostMapping(consumes = {"multipart/form-data"})
    @ResponseStatus(HttpStatus.CREATED)
    public PostResponse createPost(
            @Valid @RequestPart("post") CreatePostRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication
    ) {
        return postService.createPost(request, image, authentication.getName());
    }

    @Operation(summary = "Update a post with optional new image")
    @PutMapping(value = "/{postId}", consumes = {"multipart/form-data"})
    public PostResponse updatePost(
            @PathVariable Long postId,
            @Valid @RequestPart("post") CreatePostRequest request,
            @RequestPart(value = "image", required = false) MultipartFile image,
            Authentication authentication
    ) {
        return postService.updatePost(postId, request, image, authentication.getName());
    }

    @Operation(summary = "Delete a post")
    @DeleteMapping("/{postId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deletePost(@PathVariable Long postId, Authentication authentication) {
        postService.deletePost(postId, authentication.getName());
    }

    @Operation(summary = "Like a post")
    @PostMapping("/{postId}/like")
    public PostResponse likePost(@PathVariable Long postId, Authentication authentication) {
        return postService.likePost(postId, authentication.getName());
    }

    @Operation(summary = "Remove like from a post")
    @DeleteMapping("/{postId}/like")
    public PostResponse unlikePost(@PathVariable Long postId, Authentication authentication) {
        return postService.unlikePost(postId, authentication.getName());
    }

    @Operation(summary = "Save a post")
    @PostMapping("/{postId}/save")
    public PostResponse savePost(@PathVariable Long postId, Authentication authentication) {
        return postService.savePost(postId, authentication.getName());
    }

    @Operation(summary = "Remove a post from saved")
    @DeleteMapping("/{postId}/save")
    public PostResponse unsavePost(@PathVariable Long postId, Authentication authentication) {
        return postService.unsavePost(postId, authentication.getName());
    }

    @Operation(summary = "Get comments for a post")
    @GetMapping("/{postId}/comments")
    public List<CommentResponse> getPostComments(@PathVariable Long postId) {
        return commentService.getPostComments(postId);
    }

    @Operation(summary = "Add comment to a post")
    @PostMapping("/{postId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentResponse createComment(
            @PathVariable Long postId,
            @Valid @RequestBody CommentRequest request,
            Authentication authentication
    ) {
        return commentService.createComment(postId, request, authentication.getName());
    }
}
