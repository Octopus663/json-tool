package com.example.jsontool.service;

import com.example.jsontool.model.JSONDocument;
import com.example.jsontool.repository.JSONDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class DocumentService {

    @Autowired
    private JSONDocumentRepository documentRepository;

    public List<JSONDocument> findAll() {
        return documentRepository.findAll();
    }

    public void save(JSONDocument document) {
        if (document.getName() == null || document.getName().trim().isEmpty()) {
            throw new IllegalArgumentException("Назва не може бути порожньою");
        }
        documentRepository.save(document);
    }

    public void delete(JSONDocument document) {
        documentRepository.delete(document);
    }
}