package com.example.jsontool.service;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SyntaxHighlighterServiceTest {

    private final SyntaxHighlighterService service = new SyntaxHighlighterService();

    @Test
    void escapesHtmlInValues() {
        String html = service.highlightJson("{\"<b>\": \"<script>alert(1)</script>\"}");

        assertThat(html).doesNotContain("<script>").doesNotContain("<b>");
        assertThat(html).contains("&lt;script&gt;alert(1)&lt;/script&gt;");
    }

    @Test
    void escapesErrorMessageForInvalidJson() {
        String html = service.highlightJson("{\"a\": <img src=x onerror=alert(1)>}");

        assertThat(html).startsWith("<span style='color:red'>Invalid JSON:");
        assertThat(html).doesNotContain("<img");
    }

    @Test
    void highlightsAllTokenTypes() {
        String html = service.highlightJson("{\"s\": \"x\", \"n\": -1.5e3, \"b\": false, \"z\": null}");

        assertThat(html).contains("title='JSON Key'>&quot;s&quot;</span>");
        assertThat(html).contains("title='String Value'>&quot;x&quot;</span>");
        assertThat(html).contains("title='Numeric Value'>-1500.0</span>");
        assertThat(html).contains("title='Boolean Value'>false</span>");
        assertThat(html).contains("title='Null Value'>null</span>");
    }

    @Test
    void handlesEscapedQuotesInsideStrings() {
        String html = service.highlightJson("{\"quote\": \"say \\\"hi\\\" : 1\"}");

        assertThat(html).contains("title='String Value'>&quot;say \\&quot;hi\\&quot; : 1&quot;</span>");
        assertThat(html).doesNotContain("Numeric Value");
    }

    @Test
    void returnsEmptyStringForEmptyInput() {
        assertThat(service.highlightJson(null)).isEmpty();
        assertThat(service.highlightJson("")).isEmpty();
    }
}
