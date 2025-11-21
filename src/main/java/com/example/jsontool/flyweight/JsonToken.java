package com.example.jsontool.flyweight;

public class JsonToken {
    private String content;
    private JsonStyle style;

    public JsonToken(String content, String tokenType) {
        this.content = content;
        this.style = JsonStyleFactory.getStyle(tokenType);
    }

    public String render() {
        return style.applyStyle(content);
    }
}