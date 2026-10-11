package com.example.jsontool.controller;

import com.example.jsontool.dto.DocumentDto;
import com.example.jsontool.dto.DocumentRequest;
import com.example.jsontool.model.JSONDocument;
import com.example.jsontool.service.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
public class DocumentRestController {

    private final DocumentService documentService;

    public DocumentRestController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @GetMapping
    public List<DocumentDto> getAllDocuments() {
        return documentService.findAll().stream()
                .map(DocumentDto::from)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<DocumentDto> getDocumentById(@PathVariable Long id) {
        return documentService.findById(id)
                .map(DocumentDto::from)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> createDocument(@RequestBody DocumentRequest request) {
        try {
            JSONDocument saved = documentService.save(new JSONDocument(request.name(), request.content(), null));
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(saved.getId())
                    .toUri();
            return ResponseEntity.created(location).body(DocumentDto.from(saved));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
