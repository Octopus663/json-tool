package com.example.jsontool.flyweight;

import java.util.HashMap;
import java.util.Map;


public class JsonStyleFactory {

    private static final Map<String, JsonStyle> styleCache = new HashMap<>();


    public static JsonStyle getStyle(String type) {
        JsonStyle style = styleCache.get(type);

        if (style == null) {
            switch (type) {
                case "KEY":
                    style = new JsonStyle("#A52A2A", "bold", "JSON Key");
                    break;
                case "STRING":
                    style = new JsonStyle("#228B22", "normal", "String Value");
                    break;
                case "NUMBER":
                    style = new JsonStyle("#0000FF", "normal", "Numeric Value");
                    break;
                case "BOOLEAN":
                    style = new JsonStyle("#800080", "bold", "Boolean Value");
                    break;
                default:
                    style = new JsonStyle("#000000", "normal", "Default Text");
            }
            styleCache.put(type, style);
            System.out.println("[Flyweight Factory]: Створено новий стиль для типу: " + type);
        } else {
            // System.out.println("[Flyweight Factory]: Використано існуючий стиль для: " + type);
        }
        return style;
    }
}