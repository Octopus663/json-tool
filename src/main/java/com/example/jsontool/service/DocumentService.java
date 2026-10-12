package com.example.jsontool.service;

import com.example.jsontool.model.JSONDocument;
import com.example.jsontool.repository.JSONDocumentRepository;
import com.example.jsontool.validation.SyntaxValidation;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DocumentService {

    private final JSONDocumentRepository documentRepository;
    private final SyntaxValidation syntaxValidation = new SyntaxValidation();

    public DocumentService(JSONDocumentRepository documentRepository) {
        this.documentRepository = documentRepository;
    }

    public List<JSONDocument> findAll() {
        return documentRepository.findAll();
    }

    public Optional<JSONDocument> findById(Long id) {
        return documentRepository.findById(id);
    }

    /**
     * Зберігає документ після перевірки назви та синтаксису JSON.
     *
     * @throws IllegalArgumentException якщо дані некоректні
     */
    public JSONDocument save(JSONDocument document) {
        if (document.getName() == null || document.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Назва не може бути порожньою");
        }
        List<String> errors = syntaxValidation.validate(document.getContent(), null);
        if (!errors.isEmpty()) {
            throw new IllegalArgumentException(String.join("; ", errors));
        }
        document.setName(document.getName().trim());
        return documentRepository.save(document);
    }

    public void delete(JSONDocument document) {
        documentRepository.delete(document);
    }
}
