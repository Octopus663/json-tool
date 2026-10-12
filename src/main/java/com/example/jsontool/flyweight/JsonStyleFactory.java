package com.example.jsontool.flyweight;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Фабрика Flyweight-об'єктів. Кеш потокобезпечний, бо використовується з різних HTTP-запитів.
 */
public class JsonStyleFactory {

    private static final Logger log = LoggerFactory.getLogger(JsonStyleFactory.class);

    private static final Map<String, JsonStyle> styleCache = new ConcurrentHashMap<>();

    private JsonStyleFactory() {
    }

    public static JsonStyle getStyle(String type) {
        return styleCache.computeIfAbsent(type, JsonStyleFactory::createStyle);
    }

    static int cacheSize() {
        return styleCache.size();
    }

    private static JsonStyle createStyle(String type) {
        log.debug("[Flyweight Factory]: Створено новий стиль для типу: {}", type);
        return switch (type) {
            case "KEY" -> new JsonStyle("#A52A2A", "bold", "JSON Key");
            case "STRING" -> new JsonStyle("#228B22", "normal", "String Value");
            case "NUMBER" -> new JsonStyle("#0000FF", "normal", "Numeric Value");
            case "BOOLEAN" -> new JsonStyle("#800080", "bold", "Boolean Value");
            case "NULL" -> new JsonStyle("#808080", "bold", "Null Value");
            default -> new JsonStyle("#000000", "normal", "Default Text");
        };
    }
}
