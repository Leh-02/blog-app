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
    @Column(nullable = false, length = 180)
    private String title;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 255)
    private String imageOriginalName;

    @Column(length = 255)
    private String imageStoredName;

    @Column(length = 120)
    private String imageContentType;

    private Long imageSize;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("createdAt ASC")
    private Set<Comment> comments = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "savedPosts")
    private Set<User> savedBy = new LinkedHashSet<>();

    @ManyToMany(mappedBy = "likedPosts")
    private Set<User> likedBy = new LinkedHashSet<>();

    @PrePersist
    public void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageOriginalName() {
        return imageOriginalName;
    }

    public void setImageOriginalName(String imageOriginalName) {
        this.imageOriginalName = imageOriginalName;
    }

    public String getImageStoredName() {
        return imageStoredName;
    }

    public void setImageStoredName(String imageStoredName) {
        this.imageStoredName = imageStoredName;
    }

    public String getImageContentType() {
        return imageContentType;
    }

    public void setImageContentType(String imageContentType) {
        this.imageContentType = imageContentType;
    }

    public Long getImageSize() {
        return imageSize;
    }

    public void setImageSize(Long imageSize) {
        this.imageSize = imageSize;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public User getAuthor() {
        return author;
    }

    public void setAuthor(User author) {
        this.author = author;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public Set<User> getSavedBy() {
        return savedBy;
    }

    public Set<User> getLikedBy() {
        return likedBy;
    }
}
