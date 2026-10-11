package com.example.jsontool.template;

import com.example.jsontool.model.JSONDocument;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class DocumentExporterTest {

    private final JSONDocument document = new JSONDocument("config", "{\"a\": 1}", null);

    @Test
    void markdownExportContainsHeaderBodyAndFooter() {
        String result = new MarkdownExporter().exportDocument(document);

        assertThat(result)
                .startsWith("# Експорт Документа: config")
                .contains("```json\n{\"a\": 1}\n```")
                .endsWith("*Згенеровано автоматично JSON Tool*");
    }

    @Test
    void txtExportContainsHeaderBodyAndFooter() {
        String result = new TxtExporter().exportDocument(document);

        assertThat(result)
                .contains("ДОКУМЕНТ: config")
                .contains("{\"a\": 1}")
                .endsWith("Кінець файлу.");
    }
}
