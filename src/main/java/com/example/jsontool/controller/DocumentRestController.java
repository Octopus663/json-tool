package com.example.jsontool.controller;

import com.example.jsontool.model.JSONDocument;
import com.example.jsontool.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/documents")
public class DocumentRestController {

    @Autowired
    private DocumentService documentService;

    @GetMapping
    public List<JSONDocument> getAllDocuments() {
        return documentService.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<JSONDocument> getDocumentById(@PathVariable Long id) {
        return documentService.findAll().stream()
                .filter(d -> d.getId().equals(id))
                .findFirst()
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<JSONDocument> createDocument(@RequestBody JSONDocument document) {
        try {
            documentService.save(document);
            return ResponseEntity.ok(document);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}