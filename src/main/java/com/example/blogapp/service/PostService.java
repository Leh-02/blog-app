package com.example.blogapp.service;

import com.example.blogapp.model.Comment;
import com.example.blogapp.model.Post;
import com.example.blogapp.model.User;
import com.example.blogapp.repository.CommentRepository;
import com.example.blogapp.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository,
                       CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.commentRepository = commentRepository;
    }

    public List<Post> findAll() { return postRepository.findAll(); }

    public Optional<Post> findById(Long id) { return postRepository.findById(id); }

    public Post save(Post post) { return postRepository.save(post); }

    public void delete(Long id) { postRepository.deleteById(id); }

    public Comment addComment(Post post, User author, String content) {
        Comment c = new Comment();
        c.setContent(content);
        c.setAuthor(author);
        c.setPost(post);
        Comment saved = commentRepository.save(c);
        post.getComments().add(saved);
        postRepository.save(post);
        return saved;
    }
}
