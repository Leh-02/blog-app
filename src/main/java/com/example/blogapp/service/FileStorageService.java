package com.example.blogapp.service;

import com.example.blogapp.exception.BadRequestException;
import com.example.blogapp.exception.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.*;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/png", "image/jpeg", "image/jpg", "image/webp", "image/gif"
    );

    private final Path uploadPath;

    public FileStorageService(@Value("${app.file.upload-dir}") String uploadDir) {
        try {
            this.uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
            Files.createDirectories(this.uploadPath);
        } catch (IOException e) {
            throw new IllegalStateException("Could not create upload directory", e);
        }
    }

    public StoredFileInfo store(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BadRequestException("Only image files are allowed");
        }

        String originalName = StringUtils.cleanPath(file.getOriginalFilename());
        String extension = "";

        int dotIndex = originalName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalName.substring(dotIndex);
        }

        String storedName = UUID.randomUUID() + extension;
        Path targetLocation = uploadPath.resolve(storedName);

        try {
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to save file", e);
        }

        String publicUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/uploads/")
                .path(storedName)
                .toUriString();

        return new StoredFileInfo(
                originalName,
                storedName,
                contentType,
                file.getSize(),
                publicUrl
        );
    }

    public void deleteIfExists(String storedName) {
        if (!StringUtils.hasText(storedName)) {
            return;
        }
        try {
            Files.deleteIfExists(uploadPath.resolve(storedName));
        } catch (IOException ignored) {
        }
    }

    public Resource loadAsResource(String storedName) {
        try {
            Path filePath = uploadPath.resolve(storedName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            }
            throw new ResourceNotFoundException("File not found");
        } catch (Exception e) {
            throw new ResourceNotFoundException("File not found");
        }
    }

    public record StoredFileInfo(
            String originalName,
            String storedName,
            String contentType,
            long size,
            String publicUrl
    ) {
    }
}
