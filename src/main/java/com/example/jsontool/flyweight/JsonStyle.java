package com.example.jsontool.flyweight;

import org.springframework.web.util.HtmlUtils;

/**
 * Flyweight: незмінний (intrinsic) стан стилю, спільний для всіх токенів одного типу.
 */
public class JsonStyle {
    private final String colorHex;
    private final String fontStyle;
    private final String description;

    public JsonStyle(String colorHex, String fontStyle, String description) {
        this.colorHex = colorHex;
        this.fontStyle = fontStyle;
        this.description = description;
    }

    /**
     * Обгортає текст у span зі стилем. Текст екранується, щоб уникнути XSS.
     */
    public String applyStyle(String text) {
        return String.format("<span style='color:%s; font-weight:%s' title='%s'>%s</span>",
                colorHex, fontStyle, HtmlUtils.htmlEscape(description), HtmlUtils.htmlEscape(text));
    }

    public String getColorHex() {
        return colorHex;
    }
}
