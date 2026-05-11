package com.example.blogapp.service;

import com.example.blogapp.dto.comment.CommentResponse;
import com.example.blogapp.dto.post.CreatePostRequest;
import com.example.blogapp.dto.post.PostResponse;
import com.example.blogapp.exception.ForbiddenException;
import com.example.blogapp.exception.ResourceNotFoundException;
import com.example.blogapp.model.Comment;
import com.example.blogapp.model.Post;
import com.example.blogapp.model.Role;
import com.example.blogapp.model.User;
import com.example.blogapp.repository.PostRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Comparator;
import java.util.List;

@Service
@Transactional(readOnly = true)
public class PostService {

    private final PostRepository postRepository;
    private final FileStorageService fileStorageService;
    private final UserService userService;
    private final AsyncNotificationService asyncNotificationService;

    public PostService(PostRepository postRepository,
                       FileStorageService fileStorageService,
                       UserService userService,
                       AsyncNotificationService asyncNotificationService) {
        this.postRepository = postRepository;
        this.fileStorageService = fileStorageService;
        this.userService = userService;
        this.asyncNotificationService = asyncNotificationService;
    }

    @Cacheable("posts")
    public List<PostResponse> getAllPosts() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Cacheable(value = "post", key = "#postId")
    public PostResponse getPostById(Long postId) {
        return toResponse(getPostEntity(postId));
    }

    public List<PostResponse> getMyPosts(String email) {
        User currentUser = userService.getByEmail(email);
        return postRepository.findAllByAuthorIdOrderByCreatedAtDesc(currentUser.getId())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional
    @CacheEvict(cacheNames = {"posts", "post"}, allEntries = true)
    public PostResponse createPost(CreatePostRequest request, MultipartFile image, String currentUserEmail) {
        User author = userService.getByEmail(currentUserEmail);

        Post post = new Post();
        post.setTitle(request.title().trim());
        post.setContent(request.content().trim());
        post.setAuthor(author);

        applyImage(post, image);

        Post saved = postRepository.save(post);
        asyncNotificationService.onPostCreated(saved);

        return toResponse(saved);
    }

    @Transactional
    @CacheEvict(cacheNames = {"posts", "post"}, allEntries = true)
    public PostResponse updatePost(Long postId, CreatePostRequest request, MultipartFile image, String currentUserEmail) {
        Post post = getPostEntity(postId);
        User currentUser = userService.getByEmail(currentUserEmail);
        validateOwnerOrAdmin(post.getAuthor(), currentUser);

        post.setTitle(request.title().trim());
        post.setContent(request.content().trim());

        if (image != null && !image.isEmpty()) {
            fileStorageService.deleteIfExists(post.getImageStoredName());
            applyImage(post, image);
        }

        return toResponse(postRepository.save(post));
    }

    @Transactional
    @CacheEvict(cacheNames = {"posts", "post"}, allEntries = true)
    public void deletePost(Long postId, String currentUserEmail) {
        Post post = getPostEntity(postId);
        User currentUser = userService.getByEmail(currentUserEmail);
        validateOwnerOrAdmin(post.getAuthor(), currentUser);

        fileStorageService.deleteIfExists(post.getImageStoredName());
        postRepository.delete(post);
    }

    @Transactional
    @CacheEvict(cacheNames = {"posts", "post"}, allEntries = true)
    public PostResponse likePost(Long postId, String currentUserEmail) {
        Post post = getPostEntity(postId);
        User currentUser = userService.getByEmail(currentUserEmail);
        currentUser.getLikedPosts().add(post);
        post.getLikedBy().add(currentUser);
        return toResponse(post);
    }

    @Transactional
    @CacheEvict(cacheNames = {"posts", "post"}, allEntries = true)
    public PostResponse unlikePost(Long postId, String currentUserEmail) {
        Post post = getPostEntity(postId);
        User currentUser = userService.getByEmail(currentUserEmail);
        currentUser.getLikedPosts().remove(post);
        post.getLikedBy().remove(currentUser);
        return toResponse(post);
    }

    @Transactional
    @CacheEvict(cacheNames = {"posts", "post"}, allEntries = true)
    public PostResponse savePost(Long postId, String currentUserEmail) {
        Post post = getPostEntity(postId);
        User currentUser = userService.getByEmail(currentUserEmail);
        currentUser.getSavedPosts().add(post);
        post.getSavedBy().add(currentUser);
        return toResponse(post);
    }

    @Transactional
    @CacheEvict(cacheNames = {"posts", "post"}, allEntries = true)
    public PostResponse unsavePost(Long postId, String currentUserEmail) {
        Post post = getPostEntity(postId);
        User currentUser = userService.getByEmail(currentUserEmail);
        currentUser.getSavedPosts().remove(post);
        post.getSavedBy().remove(currentUser);
        return toResponse(post);
    }

    public Post getPostEntity(Long postId) {
        return postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));
    }

    public PostResponse toResponse(Post post) {
        List<CommentResponse> comments = post.getComments()
                .stream()
                .sorted(Comparator.comparing(Comment::getCreatedAt))
                .map(comment -> new CommentResponse(
                        comment.getId(),
                        comment.getContent(),
                        comment.getCreatedAt(),
                        comment.getUpdatedAt(),
                        userService.toSummary(comment.getAuthor())
                ))
                .toList();

        String imageUrl = post.getImageStoredName() == null
                ? null
                : "/uploads/" + post.getImageStoredName();

        return new PostResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getCreatedAt(),
                post.getUpdatedAt(),
                userService.toSummary(post.getAuthor()),
                imageUrl,
                post.getImageOriginalName(),
                post.getImageContentType(),
                post.getImageSize(),
                post.getLikedBy().size(),
                post.getSavedBy().size(),
                comments.size(),
                comments
        );
    }

    private void applyImage(Post post, MultipartFile image) {
        if (image == null || image.isEmpty()) {
            return;
        }

        FileStorageService.StoredFileInfo stored = fileStorageService.store(image);
        post.setImageOriginalName(stored.originalName());
        post.setImageStoredName(stored.storedName());
        post.setImageContentType(stored.contentType());
        post.setImageSize(stored.size());
    }

    private void validateOwnerOrAdmin(User owner, User currentUser) {
        boolean isOwner = owner.getId().equals(currentUser.getId());
        boolean isAdmin = currentUser.getRole() == Role.ADMIN;

        if (!isOwner && !isAdmin) {
            throw new ForbiddenException("You do not have permission to modify this resource");
        }
    }
}
