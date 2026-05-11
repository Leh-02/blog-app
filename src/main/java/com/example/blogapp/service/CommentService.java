package com.example.blogapp.service;

import com.example.blogapp.dto.comment.CommentRequest;
import com.example.blogapp.dto.comment.CommentResponse;
import com.example.blogapp.exception.ForbiddenException;
import com.example.blogapp.exception.ResourceNotFoundException;
import com.example.blogapp.model.Comment;
import com.example.blogapp.model.Post;
import com.example.blogapp.model.Role;
import com.example.blogapp.model.User;
import com.example.blogapp.repository.CommentRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional(readOnly = true)
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostService postService;
    private final UserService userService;
    private final AsyncNotificationService asyncNotificationService;

    public CommentService(CommentRepository commentRepository,
                          PostService postService,
                          UserService userService,
                          AsyncNotificationService asyncNotificationService) {
        this.commentRepository = commentRepository;
        this.postService = postService;
        this.userService = userService;
        this.asyncNotificationService = asyncNotificationService;
    }

    public List<CommentResponse> getPostComments(Long postId) {
        return commentRepository.findAllByPostIdOrderByCreatedAtAsc(postId)
                .stream()
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        comment.getUpdatedAt(),
                        userService.toSummary(comment.getAuthor())
                ))
                .toList();
    }

    @Transactional
    @CacheEvict(cacheNames = {"posts", "post"}, allEntries = true)
    public CommentResponse createComment(Long postId, CommentRequest request, String currentUserEmail) {
        Post post = postService.getPostEntity(postId);
        User currentUser = userService.getByEmail(currentUserEmail);

        Comment comment = new Comment();
        comment.setContent(request.content().trim());
        comment.setAuthor(currentUser);
        comment.setPost(post);

        Comment saved = commentRepository.save(comment);
        asyncNotificationService.onCommentCreated(saved);

        return new CommentResponse(
                saved.getId(),
                saved.getContent(),
                saved.getCreatedAt(),
                saved.getUpdatedAt(),
                userService.toSummary(saved.getAuthor())
        );
    }

    @Transactional
    @CacheEvict(cacheNames = {"posts", "post"}, allEntries = true)
    public void deleteComment(Long commentId, String currentUserEmail) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        User currentUser = userService.getByEmail(currentUserEmail);
        boolean isOwner = comment.getAuthor().getId().equals(currentUser.getId());
        boolean isPostAuthor = comment.getPost().getAuthor().getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isPostAuthor && !isAdmin) {
            throw new ForbiddenException("You do not have permission to delete this comment");
        }

        commentRepository.delete(comment);
    }
}
