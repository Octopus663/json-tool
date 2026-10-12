package com.example.jsontool.command;

import com.example.jsontool.model.JSONDocument;
import com.example.jsontool.service.DocumentService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CreateDocumentCommand implements Command {

    private static final Logger log = LoggerFactory.getLogger(CreateDocumentCommand.class);

    private final DocumentService documentService;
    private final JSONDocument document;

    public CreateDocumentCommand(DocumentService documentService, JSONDocument document) {
        this.documentService = documentService;
        this.document = document;
    }

    /**
     * Помилки валідації (IllegalArgumentException) пробрасуються далі,
     * щоб контролер міг показати їх користувачу.
     */
    @Override
    public boolean execute() {
        documentService.save(document);
        return true;
    }

    @Override
    public boolean undo() {
        try {
            documentService.delete(document);
            return true;
        } catch (Exception e) {
            log.error("Не вдалося скасувати створення документа id={}", document.getId(), e);
            return false;
        }
    }
}
