package com.volna.restaurantservice.service;

import com.volna.restaurantservice.exception.FileStorageException;
import com.volna.restaurantservice.storage.StorageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
public class FileStorageService {

    private final StorageService storageService;
    private final Path fileStorageLocation;
    private static final List<String> ALLOWED_EXTENSIONS = Arrays.asList(".jpg", ".jpeg", ".png", ".webp");

    public FileStorageService(
            StorageService storageService,
            @Value("${app.storage.upload-dir:uploads}") String uploadDir) {
        this.storageService = storageService;
        this.fileStorageLocation = Paths.get(uploadDir).toAbsolutePath().normalize();
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new FileStorageException("Could not create directory for upload storage.", ex);
        }
    }

    public String storeFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new FileStorageException("Filename cannot be empty");
        }

        String extension = "";
        int dotIndex = originalFilename.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFilename.substring(dotIndex).toLowerCase();
        }

        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new FileStorageException("Invalid file extension. Only JPG, PNG, and WebP are allowed.");
        }

        try {
            return storageService.store(file);
        } catch (IOException ex) {
            throw new FileStorageException("Could not store file " + originalFilename + ". Please try again!", ex);
        }
    }

    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.trim().isEmpty() || fileUrl.startsWith("http")) {
            return;
        }
        try {
            String filename = fileUrl.replace("/uploads/", "");
            Path filePath = this.fileStorageLocation.resolve(filename).normalize();
            Files.deleteIfExists(filePath);
            log.info("Deleted file: {}", filePath);
        } catch (IOException ex) {
            log.warn("Could not delete file at URL: {}", fileUrl, ex);
        }
    }
}
