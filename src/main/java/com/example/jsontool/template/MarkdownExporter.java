package com.example.jsontool.template;

import com.example.jsontool.model.JSONDocument;
import org.springframework.stereotype.Component;


@Component
public class MarkdownExporter extends DocumentExporter {

    @Override
    protected String generateHeader(JSONDocument document) {
        return "# Експорт Документа: " + document.getName();
    }

    @Override
    protected String generateBody(JSONDocument document) {
        return "## Зміст JSON\n```json\n" + document.getContent() + "\n```";
    }

    @Override
    protected String generateFooter(JSONDocument document) {
        return "---\n*Згенеровано автоматично JSON Tool*";
    }
}