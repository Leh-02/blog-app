package com.example.blogapp.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    private String title;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String content;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private Set<Comment> comments = new LinkedHashSet<>();

    // користувачі, які зберегли пост
    @ManyToMany(mappedBy = "savedPosts")
    private Set<User> savedBy = new LinkedHashSet<>();

    // користувачі, які лайкнули пост
    @ManyToMany(mappedBy = "likedPosts")
    private Set<User> likedBy = new LinkedHashSet<>();

    public Post() {
    }

    public Post(String title, String content, User author) {
        this.title = title;
        this.content = content;
        this.author = author;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public User getAuthor() { return author; }
    public void setAuthor(User author) { this.author = author; }

    public Set<Comment> getComments() { return comments; }
    public void setComments(Set<Comment> comments) { this.comments = comments; }

    public Set<User> getSavedBy() { return savedBy; }
    public void setSavedBy(Set<User> savedBy) { this.savedBy = savedBy; }

    public Set<User> getLikedBy() { return likedBy; }
    public void setLikedBy(Set<User> likedBy) { this.likedBy = likedBy; }
}
