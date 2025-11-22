package com.example.jsontool.flyweight;


public class JsonStyle {
    private final String colorHex;
    private final String fontStyle;
    private final String description;

    public JsonStyle(String colorHex, String fontStyle, String description) {
        this.colorHex = colorHex;
        this.fontStyle = fontStyle;
        this.description = description;
    }

    public String applyStyle(String text) {
        return String.format("<span style='color:%s; font-weight:%s' title='%s'>%s</span>",
                colorHex, fontStyle, description, text);
    }

    public String getColorHex() {
        return colorHex;
    }
}