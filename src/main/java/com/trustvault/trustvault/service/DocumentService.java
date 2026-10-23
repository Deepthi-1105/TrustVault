

package com.trustvault.trustvault.service;

import com.trustvault.trustvault.entity.Document;
import com.trustvault.trustvault.entity.User;
import com.trustvault.trustvault.repository.DocumentRepository;
import com.trustvault.trustvault.repository.UserRepository;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DocumentService {

    private final DocumentRepository documentRepository;
    private final UserRepository userRepository;

    public DocumentService(
            DocumentRepository documentRepository,
            UserRepository userRepository) {
        this.documentRepository = documentRepository;
        this.userRepository = userRepository;
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

    private User findUser(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found"));
    }
}
