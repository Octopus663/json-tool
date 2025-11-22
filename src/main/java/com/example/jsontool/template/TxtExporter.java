package com.example.jsontool.template;

import com.example.jsontool.model.JSONDocument;
import org.springframework.stereotype.Component;

@Component
public class TxtExporter extends DocumentExporter {

    @Override
    protected String generateHeader(JSONDocument document) {
        return "========================================\n" +
                "ДОКУМЕНТ: " + document.getName() + "\n" +
                "========================================";
    }

    @Override
    protected String generateBody(JSONDocument document) {
        return document.getContent();
    }

    @Override
    protected String generateFooter(JSONDocument document) {
        return "\n========================================\n" +
                "Кінець файлу.";
    }
}