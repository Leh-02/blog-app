package com.example.blogapp.repository;

import com.example.blogapp.model.Post;
import com.example.blogapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {
    List<Post> findAllByAuthor(User author);
}
