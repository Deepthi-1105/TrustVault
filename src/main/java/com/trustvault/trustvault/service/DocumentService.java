


package com.trustvault.trustvault.service;

import com.trustvault.trustvault.entity.Document;
import com.trustvault.trustvault.entity.User;
import com.trustvault.trustvault.repository.DocumentRepository;
import com.trustvault.trustvault.repository.UserRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;
    private final FileStorageService fileStorageService;

    public DocumentService(
            DocumentRepository documentRepository,
            UserRepository userRepository,
            FileStorageService fileStorageService) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
        this.fileStorageService = fileStorageService;
    }

    public List<Document> getDocumentsForUser(String username) {
        User currentUser = findUser(username);
        return documentRepository.findByOwner(currentUser);
    }

    public Document createDocument(
            String title,
            String description,
            String username) {

        User currentUser = findUser(username);

        Document document = new Document();
        document.setTitle(title);
        document.setDescription(description);
        document.setOwner(currentUser);

        return documentRepository.save(document);
    }

    @Transactional
    public Document uploadDocument(
            String title,
            String description,
            String username,
            MultipartFile file) throws IOException {

        if (title == null || title.isBlank()) {
            throw new IllegalArgumentException("Title is required");
        }

        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Select a file to upload");
        }

        if (file.getSize() > 10L * 1024 * 1024) {
            throw new IllegalArgumentException("Maximum file size is 10 MB");
        }

        User currentUser = findUser(username);

        String storedName = fileStorageService.storeFile(file);

        try {
            Document document = new Document();
            document.setTitle(title);
            document.setDescription(description);
            document.setOwner(currentUser);
            document.setFileName(storedName);

            return documentRepository.save(document);
        } catch (RuntimeException ex) {
            // Avoid leaving an orphaned file if database persistence fails.
            try {
                Path storedPath = fileStorageService.getFilePath(storedName);
                Files.deleteIfExists(storedPath);
            } catch (IOException | IllegalArgumentException cleanupError) {
                ex.addSuppressed(cleanupError);
            }
            throw ex;
        }
    }

    public Document getDocumentForUser(
            Long documentId, String username) {

        User currentUser = findUser(username);

        Document document = documentRepository.findById(documentId)
                .orElseThrow(() ->
                        new IllegalArgumentException("Document not found"));

        if (document.getOwner() == null
                || !document.getOwner().getId()
                        .equals(currentUser.getId())) {
            throw new AccessDeniedException("Access denied");
        }

        return document;
    }

    public Path getDownloadPath(Long documentId, String username)
        throws IOException {

    Document document = getDocumentForUser(documentId, username);

    String storedName = document.getFileName();

    if (storedName == null || storedName.isBlank()) {
        throw new IllegalArgumentException(
                "This document has no uploaded file");
    }

    Path path = fileStorageService.getFilePath(storedName);

    if (!Files.isRegularFile(path)) {
        throw new IllegalArgumentException("File not found");
    }

    return path;
}

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
    }
}
