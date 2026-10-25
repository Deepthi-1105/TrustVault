
package com.trustvault.trustvault.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path storageLocation;

    public FileStorageService(
            @Value("${trustvault.storage.location:trustvault-private-files}")
            String storageDirectory) throws IOException {

        this.storageLocation = Paths.get(storageDirectory)
                .toAbsolutePath()
                .normalize();

        Files.createDirectories(this.storageLocation);
    }

    public String storeFile(MultipartFile file) throws IOException {

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File cannot be empty");
        }

        String originalName = StringUtils.cleanPath(
                file.getOriginalFilename() == null
                        ? ""
                        : file.getOriginalFilename());

        if (originalName.isBlank()
                || originalName.contains("..")
                || originalName.contains("/")
                || originalName.contains("\\")) {
            throw new IllegalArgumentException("Invalid file name");
        }

        String storedName = UUID.randomUUID().toString();

        Path target = storageLocation.resolve(storedName).normalize();

        if (!target.startsWith(storageLocation)) {
            throw new IllegalArgumentException("Invalid storage path");
        }

        Files.copy(file.getInputStream(), target);

        return storedName;
    }

    public Path getFilePath(String storedName) {

        if (storedName == null
                || !storedName.matches(
                        "[0-9a-fA-F]{8}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{4}-[0-9a-fA-F]{12}")) {
            throw new IllegalArgumentException("Invalid stored file ID");
        }

        Path target = storageLocation.resolve(storedName).normalize();

        if (!target.startsWith(storageLocation)) {
            throw new IllegalArgumentException("Invalid storage path");
        }

        return target;
    }
}
