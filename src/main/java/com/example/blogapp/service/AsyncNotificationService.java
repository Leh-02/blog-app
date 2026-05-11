package com.example.blogapp.service;

import com.example.blogapp.model.Comment;
import com.example.blogapp.model.Post;
import com.example.blogapp.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class AsyncNotificationService {

    private static final Logger log = LoggerFactory.getLogger(AsyncNotificationService.class);

    @Async("taskExecutor")
    public void onUserRegistered(User user) {
        log.info("Async event: user registered -> {}", user.getEmail());
    }

    @Async("taskExecutor")
    public void onPostCreated(Post post) {
        log.info("Async event: post created -> id={}, title={}", post.getId(), post.getTitle());
    }

    @Async("taskExecutor")
    public void onCommentCreated(Comment comment) {
        log.info("Async event: comment created -> id={}, postId={}", comment.getId(), comment.getPost().getId());
    }
}
