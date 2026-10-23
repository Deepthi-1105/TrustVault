
package com.trustvault.trustvault;

import com.trustvault.trustvault.entity.Document;
import com.trustvault.trustvault.entity.User;
import com.trustvault.trustvault.repository.DocumentRepository;
import com.trustvault.trustvault.repository.UserRepository;
import com.trustvault.trustvault.service.DocumentService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.access.AccessDeniedException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

class DocumentServiceTest {

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private DocumentService documentService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void differentUserCannotAccessDocument() {
        User owner = new User();
        owner.setId(1L);
        owner.setUsername("owner");

        User otherUser = new User();
        otherUser.setId(2L);
        otherUser.setUsername("second");

        Document document = new Document();
        document.setId(1L);
        document.setTitle("Private document");
        document.setOwner(owner);

        when(userRepository.findByUsername("second"))
                .thenReturn(Optional.of(otherUser));

        when(documentRepository.findById(1L))
                .thenReturn(Optional.of(document));

        assertThrows(
                AccessDeniedException.class,
                () -> documentService.getDocumentForUser(1L, "second")
        );
    }
}
