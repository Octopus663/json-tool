package com.example.jsontool.command;

import com.example.jsontool.model.JSONDocument;
import com.example.jsontool.service.DocumentService;

public class CreateDocumentCommand implements Command {

    private DocumentService documentService;
    private JSONDocument document;

    public CreateDocumentCommand(DocumentService documentService, JSONDocument document) {
        this.documentService = documentService;
        this.document = document;
    }

    @Override
    public boolean execute() {
        try {
            documentService.save(document);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public boolean undo() {
        try {
            documentService.delete(document);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}