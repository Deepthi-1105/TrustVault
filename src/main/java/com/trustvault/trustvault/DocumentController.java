
package com.trustvault.trustvault;

import com.trustvault.trustvault.entity.Document;
import com.trustvault.trustvault.service.DocumentService;

import org.springframework.security.core.Authentication;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.trustvault.trustvault.service.AuditLogService;

import java.util.List;

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
                username,
                "READ_DOCUMENT",
                "SUCCESS");

        return new DocumentView(
                document.getId(),
                document.getTitle(),
                document.getDescription());

    } catch (AccessDeniedException ex) {

        auditLogService.recordEvent(
                username,
                "READ_DOCUMENT",
                "DENIED");

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Document not found");

    } catch (IllegalArgumentException ex) {

        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "Document not found");
    }
}



    // @GetMapping("/documents/{id}")
    // @ResponseBody
    // public Document getDocument(
    //         @PathVariable Long id,
    //         Authentication authentication) {

    //     try {
    //         return documentService.getDocumentForUser(
    //                 id, authentication.getName());
    //     } catch (AccessDeniedException ex) {
    //         throw new ResponseStatusException(
    //                 HttpStatus.NOT_FOUND, "Document not found");
    //     } catch (IllegalArgumentException ex) {
    //         throw new ResponseStatusException(
    //                 HttpStatus.NOT_FOUND, "Document not found");
    //     }
    // }



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
            username,
            "CREATE_DOCUMENT",
            "SUCCESS");

    redirectAttributes.addFlashAttribute(
            "success",
            "Document record created successfully.");

    return "redirect:/documents";
}



}
