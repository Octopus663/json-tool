package com.example.jsontool.template;

import com.example.jsontool.model.JSONDocument;


public abstract class DocumentExporter {

    public final String exportDocument(JSONDocument document) {
        StringBuilder sb = new StringBuilder();

        sb.append(generateHeader(document));
        sb.append("\n");
        sb.append(generateBody(document));
        sb.append("\n");
        sb.append(generateFooter(document));

        return sb.toString();
    }


    protected abstract String generateHeader(JSONDocument document);

    protected abstract String generateBody(JSONDocument document);

    protected abstract String generateFooter(JSONDocument document);
}