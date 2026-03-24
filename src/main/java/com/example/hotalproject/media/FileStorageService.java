package com.example.hotalproject.media;

import com.example.hotalproject.HotelCatalog.Utility.Exceptions.BusinessValidationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Set;
import java.util.UUID;

@Service
public class FileStorageService {

    private static final Set<String> ALLOWED_CONTENT_TYPES = Set.of(
            "image/jpeg",
            "image/png",
            "image/webp",
            "image/gif"
    );
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp", "gif");

    private final Path uploadBasePath;

    public FileStorageService(@Value("${app.upload.base-dir:uploads}") String baseDir) {
        this.uploadBasePath = Paths.get(baseDir).toAbsolutePath().normalize();
    }

    public String storeHotelImage(Long hotelId, MultipartFile file, String oldRelativePath) {
        return storeImage("hotels", hotelId, file, oldRelativePath);
    }

    public String storeRoomTypeImage(Long roomTypeId, MultipartFile file, String oldRelativePath) {
        return storeImage("room-types", roomTypeId, file, oldRelativePath);
    }

    private String storeImage(String folder, Long ownerId, MultipartFile file, String oldRelativePath) {
        validateFile(file);
        String extension = extractExtension(file.getOriginalFilename());
        String fileName = ownerId + "-" + UUID.randomUUID() + "." + extension;
        Path targetDirectory = uploadBasePath.resolve(folder);
        Path targetPath = targetDirectory.resolve(fileName);

        try {
            Files.createDirectories(targetDirectory);
            Files.copy(file.getInputStream(), targetPath, StandardCopyOption.REPLACE_EXISTING);
            deleteOldFileIfExists(oldRelativePath);
            return "/uploads/" + folder + "/" + fileName;
        } catch (IOException ex) {
            throw new BusinessValidationException("Failed to store image file");
        }
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessValidationException("Image file is required");
        }

        String contentType = file.getContentType();
        if (contentType == null || !ALLOWED_CONTENT_TYPES.contains(contentType.toLowerCase())) {
            throw new BusinessValidationException("Only image files are allowed (jpeg, png, webp, gif)");
        }

        String extension = extractExtension(file.getOriginalFilename());
        if (!ALLOWED_EXTENSIONS.contains(extension)) {
            throw new BusinessValidationException("Invalid image file extension");
        }
    }

    private String extractExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new BusinessValidationException("File extension is required");
        }
        String extension = filename.substring(filename.lastIndexOf('.') + 1).toLowerCase();
        if (extension.isBlank()) {
            throw new BusinessValidationException("File extension is required");
        }
        return extension;
    }

    private void deleteOldFileIfExists(String oldRelativePath) {
        if (oldRelativePath == null || oldRelativePath.isBlank() || !oldRelativePath.startsWith("/uploads/")) {
            return;
        }
        String relativeFilePath = oldRelativePath.substring("/uploads/".length());
        Path oldFilePath = uploadBasePath.resolve(relativeFilePath).normalize();
        try {
            Files.deleteIfExists(oldFilePath);
        } catch (IOException ignored) {
            // Ignore cleanup errors; latest upload should still succeed.
        }
    }
}
