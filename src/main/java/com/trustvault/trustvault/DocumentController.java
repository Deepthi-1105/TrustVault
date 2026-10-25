
// package com.trustvault.trustvault;

// import com.trustvault.trustvault.entity.Document;
// import com.trustvault.trustvault.service.DocumentService;

// import org.springframework.security.core.Authentication;
// import org.springframework.security.access.AccessDeniedException;
// import org.springframework.stereotype.Controller;
// import org.springframework.ui.Model;
// import org.springframework.web.bind.annotation.GetMapping;
// import org.springframework.web.bind.annotation.PathVariable;
// import org.springframework.web.bind.annotation.ResponseBody;
// import org.springframework.web.server.ResponseStatusException;
// import org.springframework.http.HttpStatus;

// import org.springframework.web.bind.annotation.PostMapping;
// import org.springframework.web.bind.annotation.RequestParam;
// import org.springframework.web.servlet.mvc.support.RedirectAttributes;

// import com.trustvault.trustvault.service.AuditLogService;

// import java.util.List;

// @Controller
// public class DocumentController {
//     public record DocumentView(
//         Long id,
//         String title,
//         String description) {
// }

//     private final DocumentService documentService;
//     private final AuditLogService auditLogService;

//     public DocumentController(
//         DocumentService documentService,
//         AuditLogService auditLogService) {

//     this.documentService = documentService;
//     this.auditLogService = auditLogService;
// }


// @GetMapping("/documents")
// public String listDocuments(
//         Authentication authentication, Model model) {

//     List<Document> documents =
//             documentService.getDocumentsForUser(
//                     authentication.getName());

//     model.addAttribute("documents", documents);

//     return "documents";
// }



    

// @GetMapping("/documents/{id}")
// @ResponseBody
// public DocumentView getDocument(
//         @PathVariable Long id,
//         Authentication authentication) {

//     String username = authentication.getName();

//     try {
//         Document document =
//                 documentService.getDocumentForUser(id, username);

//         auditLogService.recordEvent(
//                 username,
//                 "READ_DOCUMENT",
//                 "SUCCESS");

//         return new DocumentView(
//                 document.getId(),
//                 document.getTitle(),
//                 document.getDescription());

//     } catch (AccessDeniedException ex) {

//         auditLogService.recordEvent(
//                 username,
//                 "READ_DOCUMENT",
//                 "DENIED");

//         throw new ResponseStatusException(
//                 HttpStatus.NOT_FOUND, "Document not found");

//     } catch (IllegalArgumentException ex) {

//         throw new ResponseStatusException(
//                 HttpStatus.NOT_FOUND, "Document not found");
//     }
// }



//     // @GetMapping("/documents/{id}")
//     // @ResponseBody
//     // public Document getDocument(
//     //         @PathVariable Long id,
//     //         Authentication authentication) {

//     //     try {
//     //         return documentService.getDocumentForUser(
//     //                 id, authentication.getName());
//     //     } catch (AccessDeniedException ex) {
//     //         throw new ResponseStatusException(
//     //                 HttpStatus.NOT_FOUND, "Document not found");
//     //     } catch (IllegalArgumentException ex) {
//     //         throw new ResponseStatusException(
//     //                 HttpStatus.NOT_FOUND, "Document not found");
//     //     }
//     // }



// @PostMapping("/documents")
// public String createDocument(
//         @RequestParam String title,
//         @RequestParam(required = false) String description,
//         Authentication authentication,
//         RedirectAttributes redirectAttributes) {

//     String username = authentication.getName();

//     documentService.createDocument(
//             title, description, username);

//     auditLogService.recordEvent(
//             username,
//             "CREATE_DOCUMENT",
//             "SUCCESS");

//     redirectAttributes.addFlashAttribute(
//             "success",
//             "Document record created successfully.");

//     return "redirect:/documents";
// }



//}

package com.trustvault.trustvault;

import com.trustvault.trustvault.entity.Document;
import com.trustvault.trustvault.service.DocumentService;
import com.trustvault.trustvault.service.AuditLogService;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

@Controller
public class DocumentController {

    public record DocumentView(
            Long id,
            String title,
            String description) {
    }

    private final DocumentService documentService;
    private final AuditLogService auditLogService;

    public DocumentController(
            DocumentService documentService,
            AuditLogService auditLogService) {
        this.documentService = documentService;
        this.auditLogService = auditLogService;
    }

    @GetMapping("/documents")
    public String listDocuments(
            Authentication authentication, Model model) {

        List<Document> documents =
                documentService.getDocumentsForUser(
                        authentication.getName());

        model.addAttribute("documents", documents);
        return "documents";
    }

    @GetMapping("/documents/{id}")
    @ResponseBody
    public DocumentView getDocument(
            @PathVariable Long id,
            Authentication authentication) {

        String username = authentication.getName();

        try {
            Document document =
                    documentService.getDocumentForUser(id, username);

            auditLogService.recordEvent(
                    username, "READ_DOCUMENT", "SUCCESS");

            return new DocumentView(
                    document.getId(),
                    document.getTitle(),
                    document.getDescription());

        } catch (AccessDeniedException ex) {
            auditLogService.recordEvent(
                    username, "READ_DOCUMENT", "DENIED");

            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Document not found");

        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Document not found");
        }
    }

    @PostMapping("/documents")
    public String createDocument(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String username = authentication.getName();

        documentService.createDocument(
                title, description, username);

        auditLogService.recordEvent(
                username, "CREATE_DOCUMENT", "SUCCESS");

        redirectAttributes.addFlashAttribute(
                "success", "Document record created successfully.");

        return "redirect:/documents";
    }

    @PostMapping("/documents/upload")
    public String uploadDocument(
            @RequestParam String title,
            @RequestParam(required = false) String description,
            @RequestParam("file") MultipartFile file,
            Authentication authentication,
            RedirectAttributes redirectAttributes) {

        String username = authentication.getName();

        try {
            if (file == null || file.isEmpty()) {
                throw new IllegalArgumentException(
                        "Please select a file.");
            }

            String originalName = file.getOriginalFilename();

            if (originalName == null || originalName.isBlank()) {
                throw new IllegalArgumentException(
                        "Invalid filename.");
            }

            String lowerName =
                    originalName.toLowerCase(Locale.ROOT);

            // Initial allowlist: PDF and plain text only.
            if (!lowerName.endsWith(".pdf")
                    && !lowerName.endsWith(".txt")) {
                throw new IllegalArgumentException(
                        "Only PDF and TXT files are allowed.");
            }

            if (file.getSize() > 10L * 1024 * 1024) {
                throw new IllegalArgumentException(
                        "Maximum file size is 10 MB.");
            }

            documentService.uploadDocument(
                    title, description, username, file);

            auditLogService.recordEvent(
                    username, "UPLOAD_DOCUMENT", "SUCCESS");

            redirectAttributes.addFlashAttribute(
                    "success", "File uploaded successfully.");

        } catch (IllegalArgumentException | IOException ex) {
            auditLogService.recordEvent(
                    username, "UPLOAD_DOCUMENT", "FAILED");

            redirectAttributes.addFlashAttribute(
                    "error", ex.getMessage());
        }

        return "redirect:/documents";
    }

    @GetMapping("/documents/{id}/download")
public ResponseEntity<InputStreamResource> downloadDocument(
        @PathVariable Long id,
        Authentication authentication) {

    String username = authentication.getName();

    try {
        Path path = documentService.getDownloadPath(id, username);

        InputStreamResource resource =
                new InputStreamResource(Files.newInputStream(path));

        return ResponseEntity.ok()
                .header(
                        HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"document-" + id + "\"")
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(Files.size(path))
                .body(resource);

    } catch (AccessDeniedException ex) {
        auditLogService.recordEvent(
                username, "DOWNLOAD_DOCUMENT", "DENIED");

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Document not found");

    } catch (IllegalArgumentException | IOException ex) {
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Document or file not found");
    }
}
}
